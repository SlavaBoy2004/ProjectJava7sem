package academy.tochkavhoda.competition.model;

import lombok.*;
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class User {
    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private UserType userType;
    private boolean isActive;

}
