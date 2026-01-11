package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.dto.request.AddParticipantRequestDto;
import academy.tochkavhoda.competition.dto.request.LoginRequestDto;
import academy.tochkavhoda.competition.dto.response.AddParticipantResponseDto;
import academy.tochkavhoda.competition.dto.response.GetParticipantsResponseDto;
import academy.tochkavhoda.competition.dto.response.LoginResponseDto;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;
import com.google.gson.Gson;
import academy.tochkavhoda.competition.dto.request.RegisterExpertRequestDto;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantRequestDto;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import java.util.List;
import java.util.Optional;


public class UserService {

    private final UserDao userDao;
    private final Gson gson = new Gson();

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public ServerResponse login(String requestJsonString) {
        try {
            LoginRequestDto req = gson.fromJson(requestJsonString, LoginRequestDto.class);

            String validationError = validateLogin(req);
            if (validationError != null) {
                return ServerResponse.error(validationError);
            }

            User user = userDao.findByLogin(req.getLogin()).orElse(null);
            if (user == null) {
                return ServerResponse.error("Неверный логин или пароль");
            }

            if (!user.matchesCredentials(req.getLogin(), req.getPassword())) {
                return ServerResponse.error("Неверный логин или пароль");
            }

            String token = user.generateNewToken();
            userDao.save(user);

            String json = gson.toJson(LoginResponseDto.builder().token(token).build());
            return ServerResponse.ok(json);

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("Некорректный JSON");
        }
    }

    public ServerResponse logout(String token) {
        if (token == null || token.isBlank()) {
            return ServerResponse.error("Токен не задан");
        }

        User user = userDao.findByToken(token).orElse(null);
        if (user == null) {
            return ServerResponse.error("Невалидный токен");
        }

        user.logout();
        userDao.save(user);

        return ServerResponse.ok("{}");
    }

    private static String validateLogin(LoginRequestDto req) {
        if (req == null) return "Некорректный JSON";
        if (req.getLogin() == null || req.getLogin().isBlank()) return "Логин пустой";
        if (req.getPassword() == null || req.getPassword().isBlank()) return "Пароль пустой";
        return null;
    }
    public ServerResponse registerParticipant(String requestJsonString) {
        try {
            RegisterParticipantRequestDto req = gson.fromJson(requestJsonString, RegisterParticipantRequestDto.class);
            if (req == null) return ServerResponse.error("Некорректный JSON");
            if (req.getLogin() == null || req.getLogin().isBlank()) return ServerResponse.error("Логин пустой");
            if (req.getPassword() == null || req.getPassword().isBlank()) return ServerResponse.error("Пароль пустой");
            if (req.getFirstName() == null || req.getFirstName().isBlank()) return ServerResponse.error("Имя пустое");
            if (req.getLastName() == null || req.getLastName().isBlank()) return ServerResponse.error("Фамилия пустая");
            if (req.getCompanyName() == null || req.getCompanyName().isBlank()) return ServerResponse.error("Название фирмы пустое");

            if (userDao.findByLogin(req.getLogin()).isPresent()) {
                return ServerResponse.error("Пользователь с таким логином уже существует");
            }

            Participant p = new Participant(
                    req.getLogin(), req.getPassword(), req.getFirstName(), req.getLastName(), req.getCompanyName()
            );
            userDao.save(p);

            return ServerResponse.ok("{}");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("Некорректный JSON");
        }
    }

    public ServerResponse registerExpert(String requestJsonString) {
        try {
            RegisterExpertRequestDto req = gson.fromJson(requestJsonString, RegisterExpertRequestDto.class);
            if (req == null) return ServerResponse.error("Некорректный JSON");
            if (req.getLogin() == null || req.getLogin().isBlank()) return ServerResponse.error("Логин пустой");
            if (req.getPassword() == null || req.getPassword().isBlank()) return ServerResponse.error("Пароль пустой");
            if (req.getFirstName() == null || req.getFirstName().isBlank()) return ServerResponse.error("Имя пустое");
            if (req.getLastName() == null || req.getLastName().isBlank()) return ServerResponse.error("Фамилия пустая");
            if (req.getExpertDirections() == null || req.getExpertDirections().isEmpty()) return ServerResponse.error("Список направлений пустой");

            if (userDao.findByLogin(req.getLogin()).isPresent()) {
                return ServerResponse.error("Пользователь с таким логином уже существует");
            }

            Expert e = new Expert(
                    req.getLogin(), req.getPassword(), req.getFirstName(), req.getLastName(), req.getExpertDirections()
            );
            userDao.save(e);

            return ServerResponse.ok("{}");
        } catch (Exception ex) {
            return ServerResponse.error("Некорректный JSON");
        }
    }

    public ServerResponse addParticipant(String token, String requestJsonString) {
        try {
            Optional<User> byToken = userDao.findByToken(token);
            if (byToken.isEmpty()) {
                return ServerResponse.error("Invalid token");
            }

            User caller = byToken.get();
            if (!caller.isAdmin() || !caller.canUseSystem()) {
                return ServerResponse.error("Access denied");
            }

            AddParticipantRequestDto req = gson.fromJson(requestJsonString, AddParticipantRequestDto.class);
            if (req == null) return ServerResponse.error("Request is empty");

            if (req.getLogin() == null || req.getLogin().trim().isEmpty()) return ServerResponse.error("Login is empty");
            if (req.getPassword() == null || req.getPassword().trim().isEmpty()) return ServerResponse.error("Password is empty");
            if (req.getFirstName() == null || req.getFirstName().trim().isEmpty()) return ServerResponse.error("First name is empty");
            if (req.getLastName() == null || req.getLastName().trim().isEmpty()) return ServerResponse.error("Last name is empty");
            if (req.getCompanyName() == null || req.getCompanyName().trim().isEmpty()) return ServerResponse.error("Company name is empty");

            if (userDao.findByLogin(req.getLogin()).isPresent()) {
                return ServerResponse.error("Login already exists");
            }

            Participant participant = new Participant(
                    req.getLogin(),
                    req.getPassword(),
                    req.getFirstName(),
                    req.getLastName(),
                    req.getCompanyName()
            );

            participant = (Participant) userDao.save(participant);

            String json = gson.toJson(new AddParticipantResponseDto(participant.getId()));
            return new ServerResponse(200, json);

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("Bad request");
        }
    }
    public ServerResponse getParticipants(String token, String requestJsonString) {
        try {
            Optional<User> byToken = userDao.findByToken(token);
            if (byToken.isEmpty()) {
                return ServerResponse.error("Invalid token");
            }

            User caller = byToken.get();
            if (!caller.isAdmin() || !caller.canUseSystem()) {
                return ServerResponse.error("Access denied");
            }

            List<Participant> participants = userDao.findParticipants();
            String json = gson.toJson(new GetParticipantsResponseDto(participants));
            return new ServerResponse(200, json);

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("Bad request");
        }
    }

}
