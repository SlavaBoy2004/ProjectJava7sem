package academy.tochkavhoda.competition.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterExpertRequestDto {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private List<String> expertDirections;
}
