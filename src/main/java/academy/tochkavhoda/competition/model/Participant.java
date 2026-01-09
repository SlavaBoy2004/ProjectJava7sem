package academy.tochkavhoda.competition.model;

import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor

public class Participant extends User {
    private String companyName;
    private List<GrantApplication> applications = new ArrayList<>();

    public Participant(String login, String password, String firstName,
                       String lastName, String companyName) {
        super(login, password, firstName, lastName);
        this.companyName = companyName;
    }

    @Override
    public String getUserRole() {
        return "PARTICIPANT";
    }
    @Override
    public boolean canPerformAction(String action) {
        return switch (action) {
            case "SUBMIT_APPLICATION" -> isActive() && canUseSystem();
            case "VIEW_APPLICATIONS" -> isActive();
            case "CANCEL_APPLICATION" -> isActive() && canUseSystem();
            case "EDIT_PROFILE" -> isActive() && canUseSystem();
            default -> false;
        };
    }

    public boolean hasActiveApplications() {
        return applications.stream().anyMatch(GrantApplication::isActive);
    }

    public List<GrantApplication> getActiveApplications() {
        return applications.stream()
                .filter(GrantApplication::isActive)
                .collect(Collectors.toList());
    }
}
