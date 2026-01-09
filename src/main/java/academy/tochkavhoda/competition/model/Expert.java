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

public class Expert extends User {
    private List<String> expertDirections = new ArrayList<>();
    private List<ExpertRating> ratings = new ArrayList<>();

    public Expert(String login, String password, String firstName,
                  String lastName, List<String> expertDirections) {
        super(login, password, firstName, lastName);
        this.expertDirections = expertDirections != null ? expertDirections : new ArrayList<>();
    }
    @Override
    public String getUserRole() {
        return "EXPERT";
    }

    @Override
    public boolean canPerformAction(String action) {
        switch (action) {
            case "RATE_APPLICATION":
                return isActive() && canUseSystem();
            case "VIEW_APPLICATIONS":
                return isActive();
            case "UPDATE_RATING":
                return isActive() && canUseSystem();
            case "VIEW_RATINGS":
                return isActive();
            default:
                return false;
        }
    }

    public boolean isExpertInDirection(String direction) {
        return expertDirections.contains(direction);
    }

    public boolean canRateApplication(GrantApplication application) {
        return canPerformAction("RATE_APPLICATION") &&
                application != null &&
                application.isActive() &&
                application.getDirections().stream().anyMatch(this::isExpertInDirection);
    }
}