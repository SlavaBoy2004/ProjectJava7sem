package academy.tochkavhoda.competition.model;

import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Admin extends User {

    public Admin(String login, String password, String firstName, String lastName) {
        super(login, password, firstName, lastName);
    }

    @Override
    public String getUserRole() {
        return "ADMIN";
    }

    @Override
    public boolean canPerformAction(String action) {
        // Админ может выполнять любые действия
        return isActive() && canUseSystem();
    }

    public boolean canManageUser(User user) {
        return canPerformAction("MANAGE_USERS") &&
                !user.isAdmin(); // Админ не может управлять другими админами
    }

    public boolean canViewAllData() {
        return canPerformAction("VIEW_ALL_DATA");
    }
}