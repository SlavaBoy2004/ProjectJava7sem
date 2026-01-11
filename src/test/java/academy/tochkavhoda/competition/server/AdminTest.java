package academy.tochkavhoda.competition.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static academy.tochkavhoda.competition.server.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    static class AddParticipantRespTmp { Long id; }

    @Test
    void addParticipant_and_getParticipants_happyPath() {
        Gson gson = new Gson();
        Server server = new Server();

        String adminLogin = uniqueLogin("admin", 1);
        String adminPass = "Admin!123";

        seedAdmin(adminLogin, adminPass);
        String adminToken = tokenFromLogin(gson, server.login(loginJson(adminLogin, adminPass)));

        String pLogin = uniqueLogin("p", 2);
        ServerResponse addResp = server.addParticipant(adminToken,
                "{\"login\":\"" + pLogin + "\",\"password\":\"Pass!123\",\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"companyName\":\"OOO Test\"}"
        );
        assertEquals(200, addResp.getResponseCode(), addResp.getResponseData());

        AddParticipantRespTmp tmp = gson.fromJson(addResp.getResponseData(), AddParticipantRespTmp.class);
        assertNotNull(tmp.id);

        ServerResponse listResp = server.getParticipants(adminToken, "{}");
        assertEquals(200, listResp.getResponseCode(), listResp.getResponseData());
        assertTrue(listResp.getResponseData().contains(pLogin), listResp.getResponseData());
    }

    @Test
    void addParticipant_notAdmin_shouldFail() {
        Gson gson = new Gson();
        Server server = new Server();

        String pass = "Pass!123";
        String pLogin = uniqueLogin("p", 10);

        assertEquals(200, server.registerParticipant(participantRegJson(pLogin, pass)).getResponseCode());
        String pToken = tokenFromLogin(gson, server.login(loginJson(pLogin, pass)));

        ServerResponse resp = server.addParticipant(pToken,
                "{\"login\":\"x\",\"password\":\"Pass!123\",\"firstName\":\"A\",\"lastName\":\"B\",\"companyName\":\"C\"}"
        );
        assertNotEquals(200, resp.getResponseCode(), resp.getResponseData());
    }

    @Test
    void getParticipants_badToken_shouldFail() {
        Server server = new Server();
        ServerResponse resp = server.getParticipants("bad-token", "{}");
        assertNotEquals(200, resp.getResponseCode(), resp.getResponseData());
    }
}
