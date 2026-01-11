package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dto.response.LoginResponseDto;
import com.google.gson.Gson;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.model.Admin;

import static org.junit.jupiter.api.Assertions.*;

final class TestSupport {

    private TestSupport() {}

    static class AppIdTmp { Long applicationId; }
    static class RatingIdTmp { Long ratingId; }

    static String uniqueLogin(String prefix, long salt) {
        return prefix + (System.currentTimeMillis() + salt);
    }

    static String participantRegJson(String login, String pass) {
        return "{\"login\":\"" + login + "\",\"password\":\"" + pass + "\",\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"companyName\":\"OOO Test\"}";
    }

    static String expertRegJson(String login, String pass, String directionsJsonArray) {
        return "{\"login\":\"" + login + "\",\"password\":\"" + pass + "\",\"firstName\":\"Petr\",\"lastName\":\"Petrov\",\"expertDirections\":" + directionsJsonArray + "}";
    }

    static String loginJson(String login, String pass) {
        return "{\"login\":\"" + login + "\",\"password\":\"" + pass + "\"}";
    }

    static String addAppJson(String title, String desc, String directionsJsonArray, double amount) {
        return "{\"title\":\"" + title + "\",\"description\":\"" + desc + "\",\"directions\":" + directionsJsonArray + ",\"requestedAmount\":" + amount + "}";
    }

    static String rateJson(long appId, int score) {
        return "{\"applicationId\":" + appId + ",\"score\":" + score + "}";
    }

    static String appIdJson(long appId) {
        return "{\"applicationId\":" + appId + "}";
    }

    static String tokenFromLogin(Gson gson, ServerResponse loginResp) {
        assertEquals(200, loginResp.getResponseCode(), loginResp.getResponseData());
        String token = gson.fromJson(loginResp.getResponseData(), LoginResponseDto.class).getToken();
        assertNotNull(token);
        return token;
    }

    static long appIdFromAddApp(Gson gson, ServerResponse addAppResp) {
        assertEquals(200, addAppResp.getResponseCode(), addAppResp.getResponseData());
        AppIdTmp tmp = gson.fromJson(addAppResp.getResponseData(), AppIdTmp.class);
        assertNotNull(tmp);
        assertNotNull(tmp.applicationId);
        return tmp.applicationId;
    }

    static void seedAdmin(String login, String pass) {
        new UserDaoImpl().save(new Admin(login, pass, "Admin", "Root"));
    }


}
