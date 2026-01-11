package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.GrantApplicationDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.dto.request.AddGrantApplicationRequestDto;
import academy.tochkavhoda.competition.dto.request.CancelGrantApplicationRequestDto;
import academy.tochkavhoda.competition.dto.response.AddGrantApplicationResponseDto;
import academy.tochkavhoda.competition.dto.response.GetMyGrantApplicationsResponseDto;
import academy.tochkavhoda.competition.model.GrantApplication;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;
import com.google.gson.Gson;

import java.util.List;

public class GrantApplicationService {

    private final UserDao userDao;
    private final GrantApplicationDao applicationDao;
    private final Gson gson = new Gson();

    public GrantApplicationService(UserDao userDao, GrantApplicationDao applicationDao) {
        this.userDao = userDao;
        this.applicationDao = applicationDao;
    }

    public ServerResponse addGrantApplication(String token, String requestJsonString) {
        try {
            User user = userDao.findByToken(token).orElse(null);
            if (user == null) return ServerResponse.error("Invalid token");
            if (!user.isParticipant() || !user.canUseSystem()) return ServerResponse.error("Access denied");

            AddGrantApplicationRequestDto req =
                    gson.fromJson(requestJsonString, AddGrantApplicationRequestDto.class);
            if (req == null) return ServerResponse.error("Некорректный JSON");

            if (req.getTitle() == null || req.getTitle().isBlank()) return ServerResponse.error("Title is empty");
            if (req.getDescription() == null || req.getDescription().isBlank()) return ServerResponse.error("Description is empty");
            if (req.getDirections() == null || req.getDirections().isEmpty()) return ServerResponse.error("Directions is empty");
            if (req.getRequestedAmount() == null || req.getRequestedAmount() <= 0) return ServerResponse.error("Requested amount is invalid");

            Participant participant = (Participant) user;

            GrantApplication app = GrantApplication.builder()
                    .title(req.getTitle())
                    .description(req.getDescription())
                    .directions(req.getDirections())
                    .requestedAmount(req.getRequestedAmount())
                    .participantId(participant.getId())
                    .isActive(true)
                    .build();

            app = applicationDao.save(app);


            String json = gson.toJson(AddGrantApplicationResponseDto.builder()
                    .applicationId(app.getId())
                    .build());

            return ServerResponse.ok(json);

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("Bad request");
        }
    }

    public ServerResponse cancelGrantApplication(String token, String requestJsonString) {
        try {
            User user = userDao.findByToken(token).orElse(null);
            if (user == null) return ServerResponse.error("Invalid token");
            if (!user.isParticipant() || !user.canUseSystem()) return ServerResponse.error("Access denied");

            CancelGrantApplicationRequestDto req =
                    gson.fromJson(requestJsonString, CancelGrantApplicationRequestDto.class);
            if (req == null || req.getApplicationId() == null) return ServerResponse.error("ApplicationId is empty");

            Participant participant = (Participant) user;

            GrantApplication app = applicationDao.findById(req.getApplicationId()).orElse(null);
            if (app == null) return ServerResponse.error("Application not found");

            if (!participant.getId().equals(app.getParticipantId())) {
                return ServerResponse.error("Cannot cancel чужую заявку");
            }

            boolean ok = applicationDao.deactivate(req.getApplicationId());
            return ok ? ServerResponse.ok("{}") : ServerResponse.error("Cannot cancel");

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("Bad request");
        }
    }

    public ServerResponse getMyGrantApplications(String token, String requestJsonString) {
        try {
            User user = userDao.findByToken(token).orElse(null);
            if (user == null) return ServerResponse.error("Invalid token");
            if (!user.isParticipant() || !user.canUseSystem()) return ServerResponse.error("Access denied");

            Participant participant = (Participant) user;

            List<GrantApplication> list = applicationDao.findByParticipantId(participant.getId());
            String json = gson.toJson(GetMyGrantApplicationsResponseDto.builder()
                    .applications(list)
                    .build());

            return ServerResponse.ok(json);

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("Bad request");
        }
    }
}
