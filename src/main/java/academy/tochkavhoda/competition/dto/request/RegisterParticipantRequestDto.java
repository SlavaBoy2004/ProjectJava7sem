package academy.tochkavhoda.competition.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterParticipantRequestDto {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String companyName;
}
