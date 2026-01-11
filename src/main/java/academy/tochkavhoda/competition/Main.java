package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.response.LoginResponseDto;
import academy.tochkavhoda.competition.server.Server;
import academy.tochkavhoda.competition.server.ServerResponse;
import com.google.gson.Gson;

public class Main {
    static class Tmp {
        Long applicationId;
    }
    public static void main(String[] args) {
        Gson gson = new Gson();
        Server server = new Server();

        // --------- 1) REGISTER + LOGIN PARTICIPANT ----------
        String regParticipantJson =
                "{\"login\":\"p1\",\"password\":\"Pass!123\",\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"companyName\":\"OOO Test\"}";
        ServerResponse regResp = server.registerParticipant(regParticipantJson);
        System.out.println("registerParticipant code=" + regResp.getResponseCode());
        System.out.println("registerParticipant data=" + regResp.getResponseData());

        String loginParticipantJson =
                "{\"login\":\"p1\",\"password\":\"Pass!123\"}";
        ServerResponse loginParticipantResp = server.login(loginParticipantJson);
        System.out.println("login(participant) code=" + loginParticipantResp.getResponseCode());
        System.out.println("login(participant) data=" + loginParticipantResp.getResponseData());

        String participantToken = null;
        if (loginParticipantResp.getResponseCode() == 200) {
            participantToken = gson.fromJson(loginParticipantResp.getResponseData(), LoginResponseDto.class).getToken();
        }

        // --------- 2) PARTICIPANT ADDS APPLICATION ----------
        Long appId = null;
        if (participantToken != null) {
            String addAppJson =
                    "{\"title\":\"AI Startup\",\"description\":\"We build AI\",\"directions\":[\"informatika\",\"matematika\"],\"requestedAmount\":1000.0}";

            ServerResponse addAppResp = server.addGrantApplication(participantToken, addAppJson);
            System.out.println("addGrantApplication code=" + addAppResp.getResponseCode());
            System.out.println("addGrantApplication data=" + addAppResp.getResponseData());

            if (addAppResp.getResponseCode() == 200) {
                Tmp tmp = gson.fromJson(addAppResp.getResponseData(), Tmp.class);
                if (tmp != null) {
                    appId = tmp.applicationId;
                }
            }


            ServerResponse myApps = server.getMyGrantApplications(participantToken, "{}");
            System.out.println("getMyGrantApplications code=" + myApps.getResponseCode());
            System.out.println("getMyGrantApplications data=" + myApps.getResponseData());
        }

        // --------- 3) REGISTER + LOGIN EXPERT ----------
        String regExpertJson =
                "{\"login\":\"e1\",\"password\":\"Pass!123\",\"firstName\":\"Petr\",\"lastName\":\"Petrov\",\"expertDirections\":[\"informatika\"]}";
        ServerResponse regExpertResp = server.registerExpert(regExpertJson);
        System.out.println("registerExpert code=" + regExpertResp.getResponseCode());
        System.out.println("registerExpert data=" + regExpertResp.getResponseData());

        String loginExpertJson =
                "{\"login\":\"e1\",\"password\":\"Pass!123\"}";
        ServerResponse loginExpertResp = server.login(loginExpertJson);
        System.out.println("login(expert) code=" + loginExpertResp.getResponseCode());
        System.out.println("login(expert) data=" + loginExpertResp.getResponseData());

        String expertToken = null;
        if (loginExpertResp.getResponseCode() == 200) {
            expertToken = gson.fromJson(loginExpertResp.getResponseData(), LoginResponseDto.class).getToken();
        }

        // --------- 4) EXPERT RATES APPLICATION ----------
        if (expertToken != null && appId != null) {
            String rateJson = "{\"applicationId\":" + appId + ",\"score\":5}";
            ServerResponse rateResp = server.rateApplication(expertToken, rateJson);
            System.out.println("rateApplication code=" + rateResp.getResponseCode());
            System.out.println("rateApplication data=" + rateResp.getResponseData());

            // update rating (проверяем "эксперт вправе изменить оценку")
            String rateJson2 = "{\"applicationId\":" + appId + ",\"score\":4}";
            ServerResponse rateResp2 = server.rateApplication(expertToken, rateJson2);
            System.out.println("rateApplication(update) code=" + rateResp2.getResponseCode());
            System.out.println("rateApplication(update) data=" + rateResp2.getResponseData());

            // list my ratings
            ServerResponse myRatings = server.getMyRatings(expertToken, "{}");
            System.out.println("getMyRatings code=" + myRatings.getResponseCode());
            System.out.println("getMyRatings data=" + myRatings.getResponseData());

            // delete rating
            String delJson = "{\"applicationId\":" + appId + "}";
            ServerResponse delResp = server.deleteMyRating(expertToken, delJson);
            System.out.println("deleteMyRating code=" + delResp.getResponseCode());
            System.out.println("deleteMyRating data=" + delResp.getResponseData());

            ServerResponse myRatings2 = server.getMyRatings(expertToken, "{}");
            System.out.println("getMyRatings(after delete) code=" + myRatings2.getResponseCode());
            System.out.println("getMyRatings(after delete) data=" + myRatings2.getResponseData());
        }

        // --------- 5) LOGOUTS ----------
        if (participantToken != null) {
            ServerResponse logoutP = server.logout(participantToken, "{}");
            System.out.println("logout(participant) code=" + logoutP.getResponseCode());
            System.out.println("logout(participant) data=" + logoutP.getResponseData());
        }
        if (expertToken != null) {
            ServerResponse logoutE = server.logout(expertToken, "{}");
            System.out.println("logout(expert) code=" + logoutE.getResponseCode());
            System.out.println("logout(expert) data=" + logoutE.getResponseData());
        }
    }
}
