package academy.tochkavhoda.competition.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static academy.tochkavhoda.competition.server.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class ExpertRatingTest {

    @Test
    void participantExpertRatingFlow() {
        Gson gson = new Gson();
        Server server = new Server();

        String pass = "Pass!123";
        String pLogin = uniqueLogin("p", 0);
        String eLogin = uniqueLogin("e", 1);

        assertEquals(200, server.registerParticipant(participantRegJson(pLogin, pass)).getResponseCode());
        String participantToken = tokenFromLogin(gson, server.login(loginJson(pLogin, pass)));

        long appId = appIdFromAddApp(gson, server.addGrantApplication(
                participantToken,
                addAppJson("AI Startup", "We build AI", "[\"informatika\",\"matematika\"]", 1000.0)
        ));

        assertEquals(200, server.registerExpert(expertRegJson(eLogin, pass, "[\"informatika\"]")).getResponseCode());
        String expertToken = tokenFromLogin(gson, server.login(loginJson(eLogin, pass)));

        ServerResponse rate1 = server.rateApplication(expertToken, rateJson(appId, 5));
        assertEquals(200, rate1.getResponseCode(), rate1.getResponseData());

        RatingIdTmp rtmp = gson.fromJson(rate1.getResponseData(), RatingIdTmp.class);
        assertNotNull(rtmp);
        assertNotNull(rtmp.ratingId);

        assertEquals(200, server.rateApplication(expertToken, rateJson(appId, 4)).getResponseCode());

        ServerResponse myRatings = server.getMyRatings(expertToken, "{}");
        assertEquals(200, myRatings.getResponseCode(), myRatings.getResponseData());
        assertTrue(myRatings.getResponseData().contains("\"applicationId\":" + appId), myRatings.getResponseData());

        assertEquals(200, server.deleteMyRating(expertToken, appIdJson(appId)).getResponseCode());
    }

    @Test
    void getMyRatings_withBadToken_shouldFail() {
        Server server = new Server();
        ServerResponse resp = server.getMyRatings("bad-token", "{}");
        assertNotEquals(200, resp.getResponseCode(), resp.getResponseData());
    }

    @Test
    void rateApplication_scoreOutOfRange_shouldFail() {
        Gson gson = new Gson();
        Server server = new Server();

        String pass = "Pass!123";
        String pLogin = uniqueLogin("p", 10);
        String eLogin = uniqueLogin("e", 11);

        assertEquals(200, server.registerParticipant(participantRegJson(pLogin, pass)).getResponseCode());
        String pToken = tokenFromLogin(gson, server.login(loginJson(pLogin, pass)));

        long appId = appIdFromAddApp(gson, server.addGrantApplication(
                pToken,
                addAppJson("AI Startup", "We build AI", "[\"informatika\"]", 1000.0)
        ));

        assertEquals(200, server.registerExpert(expertRegJson(eLogin, pass, "[\"informatika\"]")).getResponseCode());
        String eToken = tokenFromLogin(gson, server.login(loginJson(eLogin, pass)));

        ServerResponse s0 = server.rateApplication(eToken, rateJson(appId, 0));
        ServerResponse s6 = server.rateApplication(eToken, rateJson(appId, 6));

        assertAll(
                () -> assertNotEquals(200, s0.getResponseCode(), s0.getResponseData()),
                () -> assertNotEquals(200, s6.getResponseCode(), s6.getResponseData())
        );
    }

    @Test
    void rateApplication_expertWithoutDirection_shouldFail() {
        Gson gson = new Gson();
        Server server = new Server();

        String pass = "Pass!123";
        String pLogin = uniqueLogin("p", 20);
        String eLogin = uniqueLogin("e", 21);

        assertEquals(200, server.registerParticipant(participantRegJson(pLogin, pass)).getResponseCode());
        String pToken = tokenFromLogin(gson, server.login(loginJson(pLogin, pass)));

        long appId = appIdFromAddApp(gson, server.addGrantApplication(
                pToken,
                addAppJson("Math project", "Desc", "[\"matematika\"]", 100.0)
        ));

        assertEquals(200, server.registerExpert(expertRegJson(eLogin, pass, "[\"informatika\"]")).getResponseCode());
        String eToken = tokenFromLogin(gson, server.login(loginJson(eLogin, pass)));

        ServerResponse rate = server.rateApplication(eToken, rateJson(appId, 5));
        assertNotEquals(200, rate.getResponseCode(), rate.getResponseData());
    }

    @Test
    void deleteMyRating_whenNotExists_shouldFail() {
        Gson gson = new Gson();
        Server server = new Server();

        String pass = "Pass!123";
        String pLogin = uniqueLogin("p", 400);
        String eLogin = uniqueLogin("e", 401);

        assertEquals(200, server.registerParticipant(participantRegJson(pLogin, pass)).getResponseCode());
        String pToken = tokenFromLogin(gson, server.login(loginJson(pLogin, pass)));

        long appId = appIdFromAddApp(gson, server.addGrantApplication(
                pToken,
                addAppJson("App", "Desc", "[\"informatika\"]", 10.0)
        ));

        assertEquals(200, server.registerExpert(expertRegJson(eLogin, pass, "[\"informatika\"]")).getResponseCode());
        String eToken = tokenFromLogin(gson, server.login(loginJson(eLogin, pass)));

        // оценки ещё нет -> удаление должно дать ошибку
        ServerResponse del = server.deleteMyRating(eToken, appIdJson(appId));
        assertNotEquals(200, del.getResponseCode(), del.getResponseData());
    }
}
