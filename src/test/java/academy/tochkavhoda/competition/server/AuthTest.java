package academy.tochkavhoda.competition.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static academy.tochkavhoda.competition.server.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthTest {

    @Test
    void registerParticipant_duplicateLogin_shouldFail() {
        Server server = new Server();
        String pass = "Pass!123";
        String login = uniqueLogin("p", 200);

        assertEquals(200, server.registerParticipant(participantRegJson(login, pass)).getResponseCode());
        ServerResponse dup = server.registerParticipant(participantRegJson(login, pass));
        assertNotEquals(200, dup.getResponseCode(), dup.getResponseData());
    }

    @Test
    void registerExpert_duplicateLogin_shouldFail() {
        Server server = new Server();
        String pass = "Pass!123";
        String login = uniqueLogin("e", 201);

        assertEquals(200, server.registerExpert(expertRegJson(login, pass, "[\"informatika\"]")).getResponseCode());
        ServerResponse dup = server.registerExpert(expertRegJson(login, pass, "[\"informatika\"]"));
        assertNotEquals(200, dup.getResponseCode(), dup.getResponseData());
    }

    @Test
    void login_wrongPassword_shouldFail() {
        Server server = new Server();
        String pass = "Pass!123";
        String pLogin = uniqueLogin("p", 100);

        assertEquals(200, server.registerParticipant(participantRegJson(pLogin, pass)).getResponseCode());
        ServerResponse bad = server.login(loginJson(pLogin, "WrongPass!123"));
        assertNotEquals(200, bad.getResponseCode(), bad.getResponseData());
    }

    @Test
    void logout_withBadToken_shouldFail() {
        Server server = new Server();
        ServerResponse resp = server.logout("bad-token", "{}");
        assertNotEquals(200, resp.getResponseCode(), resp.getResponseData());
    }

    @Test
    void tokenAfterLogout_shouldNotWork() {
        Gson gson = new Gson();
        Server server = new Server();

        String pass = "Pass!123";
        String pLogin = uniqueLogin("p", 50);

        assertEquals(200, server.registerParticipant(participantRegJson(pLogin, pass)).getResponseCode());
        String pToken = tokenFromLogin(gson, server.login(loginJson(pLogin, pass)));

        ServerResponse logout = server.logout(pToken, "{}");
        assertEquals(200, logout.getResponseCode(), logout.getResponseData());

        ServerResponse after = server.getMyGrantApplications(pToken, "{}");
        assertNotEquals(200, after.getResponseCode(), after.getResponseData());
    }

    @Test
    void methodsRequiringToken_withNullOrEmptyToken_shouldFail() {
        Server server = new Server();

        assertAll(
                () -> assertNotEquals(200, server.getMyGrantApplications(null, "{}").getResponseCode()),
                () -> assertNotEquals(200, server.getMyGrantApplications("", "{}").getResponseCode()),
                () -> assertNotEquals(200, server.getMyRatings(null, "{}").getResponseCode()),
                () -> assertNotEquals(200, server.getMyRatings("", "{}").getResponseCode())
        );
    }
}
