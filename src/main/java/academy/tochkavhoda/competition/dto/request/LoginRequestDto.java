package academy.tochkavhoda.competition.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class LoginRequestDto {
    private String login;
    private String password;
}
