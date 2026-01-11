package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.response.ErrorResponseDto;
import com.google.gson.Gson;
import academy.tochkavhoda.competition.service.UserService;
import academy.tochkavhoda.competition.daoimpl.GrantApplicationDaoImpl;
import academy.tochkavhoda.competition.service.GrantApplicationService;
import academy.tochkavhoda.competition.daoimpl.ExpertRatingDaoImpl;
import academy.tochkavhoda.competition.service.ExpertRatingService;
import academy.tochkavhoda.competition.daoimpl.GrantApplicationDaoImpl;



public class Server {

    private final Gson gson = new Gson();
    private final UserDao userDao = new UserDaoImpl();
    private final UserService userService = new UserService(new UserDaoImpl());
    private final GrantApplicationService grantApplicationService =
            new GrantApplicationService(new UserDaoImpl(), new GrantApplicationDaoImpl());
    private final ExpertRatingService expertRatingService =
            new ExpertRatingService(new UserDaoImpl(), new GrantApplicationDaoImpl(), new ExpertRatingDaoImpl());




    private ServerResponse badRequest(String message) {
        return new ServerResponse(400, gson.toJson(new ErrorResponseDto(message)));
    }
    public ServerResponse registerParticipant(String requestJsonString) {
        return userService.registerParticipant(requestJsonString);
    }

    public ServerResponse registerExpert(String requestJsonString) {
        return userService.registerExpert(requestJsonString);
    }

    public ServerResponse login(String requestJsonString) {
        return userService.login(requestJsonString);
    }

    public ServerResponse logout(String token, String requestJsonString) {
        return userService.logout(token);
    }
    public ServerResponse addGrantApplication(String token, String requestJsonString) {
        return grantApplicationService.addGrantApplication(token, requestJsonString);
    }

    public ServerResponse cancelGrantApplication(String token, String requestJsonString) {
        return grantApplicationService.cancelGrantApplication(token, requestJsonString);
    }

    public ServerResponse getMyGrantApplications(String token, String requestJsonString) {
        return grantApplicationService.getMyGrantApplications(token, requestJsonString);
    }
    public ServerResponse rateApplication(String token, String requestJsonString) {
        return expertRatingService.rateApplication(token, requestJsonString);
    }

    public ServerResponse deleteMyRating(String token, String requestJsonString) {
        return expertRatingService.deleteMyRating(token, requestJsonString);
    }

    public ServerResponse getMyRatings(String token, String requestJsonString) {
        return expertRatingService.getMyRatings(token, requestJsonString);
    }

    public ServerResponse addParticipant(String token, String requestJsonString) {
        return userService.addParticipant(token, requestJsonString);
    }

    public ServerResponse getParticipants(String token, String requestJsonString) {
        return userService.getParticipants(token, requestJsonString);
    }



}
