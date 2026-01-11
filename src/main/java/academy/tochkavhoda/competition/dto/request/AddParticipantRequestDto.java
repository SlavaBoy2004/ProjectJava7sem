package academy.tochkavhoda.competition.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddParticipantRequestDto {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String companyName;
}
