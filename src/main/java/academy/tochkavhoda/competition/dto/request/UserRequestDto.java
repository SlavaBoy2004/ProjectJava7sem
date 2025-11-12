package academy.tochkavhoda.competition.dto.request;

import lombok.*;

@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserRequestDto {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
}
