package academy.tochkavhoda.competition.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static academy.tochkavhoda.competition.server.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class GrantApplicationTest {

    @Test
    void addGrantApplication_withoutToken_shouldFail() {
        Server server = new Server();
        ServerResponse resp = server.addGrantApplication(null,
                addAppJson("T", "D", "[\"informatika\"]", 1.0));
        assertNotEquals(200, resp.getResponseCode(), resp.getResponseData());
    }

    @Test
    void grantApplication_cancel_shouldMakeInactiveOrDisappear() {
        Gson gson = new Gson();
        Server server = new Server();

        String pass = "Pass!123";
        String pLogin = uniqueLogin("p", 30);

        assertEquals(200, server.registerParticipant(participantRegJson(pLogin, pass)).getResponseCode());
        String pToken = tokenFromLogin(gson, server.login(loginJson(pLogin, pass)));

        long appId = appIdFromAddApp(gson, server.addGrantApplication(
                pToken,
                addAppJson("Cancel me", "Desc", "[\"informatika\"]", 10.0)
        ));

        ServerResponse cancel = server.cancelGrantApplication(pToken, appIdJson(appId));
        assertEquals(200, cancel.getResponseCode(), cancel.getResponseData());

        ServerResponse myApps = server.getMyGrantApplications(pToken, "{}");
        assertEquals(200, myApps.getResponseCode(), myApps.getResponseData());

        String data = myApps.getResponseData();
        boolean disappeared = !data.contains("\"id\":" + appId);
        boolean becameInactive = data.contains("\"id\":" + appId) && data.contains("\"isActive\":false");
        assertTrue(disappeared || becameInactive, data);
    }

    @Test
    void cancelGrantApplication_withBadAppId_shouldFail() {
        Gson gson = new Gson();
        Server server = new Server();

        String pass = "Pass!123";
        String pLogin = uniqueLogin("p", 40);

        assertEquals(200, server.registerParticipant(participantRegJson(pLogin, pass)).getResponseCode());
        String pToken = tokenFromLogin(gson, server.login(loginJson(pLogin, pass)));

        ServerResponse cancel = server.cancelGrantApplication(pToken, "{\"applicationId\":999999999}");
        assertNotEquals(200, cancel.getResponseCode(), cancel.getResponseData());
    }

    @Test
    void cancelGrantApplication_otherUserApp_shouldFail() {
        Gson gson = new Gson();
        Server server = new Server();
        String pass = "Pass!123";

        String p1 = uniqueLogin("p", 300);
        String p2 = uniqueLogin("p", 301);

        assertEquals(200, server.registerParticipant(participantRegJson(p1, pass)).getResponseCode());
        assertEquals(200, server.registerParticipant(participantRegJson(p2, pass)).getResponseCode());

        String t1 = tokenFromLogin(gson, server.login(loginJson(p1, pass)));
        String t2 = tokenFromLogin(gson, server.login(loginJson(p2, pass)));

        long appId = appIdFromAddApp(gson, server.addGrantApplication(
                t1,
                addAppJson("P1 app", "Desc", "[\"informatika\"]", 10.0)
        ));

        ServerResponse cancelByOther = server.cancelGrantApplication(t2, appIdJson(appId));
        assertNotEquals(200, cancelByOther.getResponseCode(), cancelByOther.getResponseData());
    }
}
