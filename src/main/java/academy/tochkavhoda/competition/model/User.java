package academy.tochkavhoda.competition.model;

import lombok.*;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public abstract class User {
    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String token;
    private Long tokenExpiryTime;

        protected User(String login, String password, String firstName, String lastName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = true;
    }

    public abstract String getUserRole();

    public abstract boolean canPerformAction(String action);


    public boolean isParticipant() {
        return this instanceof Participant;
    }

    public boolean isExpert() {
        return this instanceof Expert;
    }

    public boolean isAdmin() {
        return this instanceof Admin;
    }


    public String generateNewToken() {
        this.token = UUID.randomUUID().toString();
        this.tokenExpiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24 часа
        return this.token;
    }

    public boolean isTokenValid() {
        return this.token != null &&
                this.tokenExpiryTime != null &&
                System.currentTimeMillis() < this.tokenExpiryTime;
    }

    public void logout() {
        this.token = null;
        this.tokenExpiryTime = null;
    }


    public boolean isValid() {
        return login != null && !login.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty();
    }

    public boolean hasValidPassword() {
        return password != null &&
                password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()].*");
    }

    public boolean hasValidLogin() {
        return login != null &&
                login.length() >= 4 &&
                login.matches("[a-zA-Z0-9_]+");
    }



    public boolean canLogin() {
        return isActive && isValid();
    }

    public boolean canUseSystem() {
        return isActive && isTokenValid();
    }



    public void deactivate() {
        this.isActive = false;
        logout(); // При деактивации разлогиниваем
    }

    public void activate() {
        this.isActive = true;
    }


    public void setLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login cannot be null or empty");
        }
        if (login.length() < 4) {
            throw new IllegalArgumentException("Login must be at least 4 characters long");
        }
        if (!login.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Login can only contain letters, numbers and underscores");
        }
        this.login = login;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        this.password = password;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.lastName = lastName;
    }

    //Информационные методы

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getStatus() {
        if (!isActive) return "INACTIVE";
        if (!isTokenValid()) return "LOGGED_OUT";
        return "ACTIVE";
    }

    //  Методы для безопасного сравнения

    public boolean matchesCredentials(String login, String password) {
        return this.login.equals(login) && this.password.equals(password);
    }

    public boolean isSameUser(User other) {
        if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.login.equals(other.login);
    }

    //Методы для удобства работы с наследованием



    public <T extends User> T asType(Class<T> userClass) {
        if (userClass.isInstance(this)) {
            return (T) this;
        }
        throw new ClassCastException(
                String.format("User '%s' is not of type %s", this.login, userClass.getSimpleName())
        );
    }

        public <T extends User> boolean isType(Class<T> userClass) {
        return userClass.isInstance(this);
    }

    // Дополнительные полезные методы

    public boolean hasToken() {
        return token != null && !token.trim().isEmpty();
    }

    public boolean isLoggedIn() {
        return hasToken() && isTokenValid();
    }

    public Long getTokenRemainingTime() {
        if (!hasToken() || tokenExpiryTime == null) {
            return 0L;
        }
        return Math.max(0, tokenExpiryTime - System.currentTimeMillis());
    }

    public String getTokenRemainingTimeFormatted() {
        long remaining = getTokenRemainingTime();
        if (remaining == 0) return "EXPIRED";

        long hours = remaining / (60 * 60 * 1000);
        long minutes = (remaining % (60 * 60 * 1000)) / (60 * 1000);
        return String.format("%02d:%02d", hours, minutes);
    }

    // Методы для установки ID (только если не установлен)

    public void setIdIfNull(Long id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    // Метод для безопасного обновления пароля

    public void updatePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword)) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        setPassword(newPassword);
    }

    // Метод для сериализации основной информации

    public String toBasicInfoString() {
        return String.format("User{id=%d, login='%s', name='%s %s', role=%s, status=%s}",
                id, login, firstName, lastName, getUserRole(), getStatus());
    }
}