package academy.tochkavhoda.competition.server;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ServerResponse {
    private int responseCode;
    private String responseData;

    public static ServerResponse ok(String json) {
        return new ServerResponse(200, json == null ? "{}" : json);
    }

    public static ServerResponse error(String message) {
        // message -> {"error": "..."}
        return new ServerResponse(400,
                "{\"error\":\"" + escapeJson(message) + "\"}");
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
