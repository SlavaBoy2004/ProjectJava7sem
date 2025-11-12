package academy.tochkavhoda.competition.dto.response;

import academy.tochkavhoda.competition.model.UserType;
import lombok.*;

@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private UserType userType;
    private boolean isActive;
}
