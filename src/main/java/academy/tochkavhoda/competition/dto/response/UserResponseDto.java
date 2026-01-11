package academy.tochkavhoda.competition.dto.response;

import lombok.*;

import java.util.List;

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
    private String role;
    private boolean isActive;
    private String companyName;
    private List<String> expertDirections;
}
