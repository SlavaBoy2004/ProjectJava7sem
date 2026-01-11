package academy.tochkavhoda.competition.database;

import academy.tochkavhoda.competition.model.*;
import lombok.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE) // Приватный конструктор
@Setter
@Getter
public class Database {
    // 1. Безопасный Singleton (Eager initialization)
    private static final Database instance = new Database();

    public static Database getInstance() {
        return instance;
    }

    // 2. Потокобезопасные коллекции
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, GrantApplication> applications = new ConcurrentHashMap<>();
    private final Map<Long, ExpertRating> ratings = new ConcurrentHashMap<>();

    // Счетчики (AtomicLong уже потокобезопасны)
    private final AtomicLong userIdCounter = new AtomicLong(1);
    private final AtomicLong applicationIdCounter = new AtomicLong(1);
    private final AtomicLong ratingIdCounter = new AtomicLong(1);

    // --- Пользователи ---

    public boolean deleteUser(Long userId) {
        return users.remove(userId) != null;
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    public <T extends User> List<T> findUsersByType(Class<T> type) {
        return users.values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setIdIfNull(userIdCounter.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findUserByLogin(String login) {
        return users.values().stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findUserByToken(String token) {
        if (token == null) return Optional.empty();
        return users.values().stream()
                .filter(user -> token.equals(user.getToken()) && user.isTokenValid())
                .findFirst();
    }

    // --- Заявки ---

    public List<GrantApplication> findApplicationsByParticipantId(Long participantId) {
        return applications.values().stream()
                .filter(app -> Objects.equals(app.getParticipantId(), participantId))
                .collect(Collectors.toList());
    }

    public List<GrantApplication> findApplicationsByDirections(List<String> directions) {
        if (directions == null || directions.isEmpty()) {
            return new ArrayList<>();
        }
        return applications.values().stream()
                .filter(app -> app.getDirections() != null && app.getDirections().stream().anyMatch(directions::contains))
                .collect(Collectors.toList());
    }

    public List<GrantApplication> findActiveApplications() {
        return applications.values().stream()
                .filter(GrantApplication::isActive)
                .collect(Collectors.toList());
    }

    public boolean deactivateApplication(Long applicationId) {
        GrantApplication app = applications.get(applicationId);
        if (app == null) return false;
        app.setActive(false);
        // В ConcurrentHashMap объекты хранятся по ссылке,
        // но для надежности можно явно обновить, хотя set делает put
        applications.put(app.getId(), app);
        return true;
    }

    public GrantApplication saveApplication(GrantApplication application) {
        if (application.getId() == null) {
            application.setId(applicationIdCounter.getAndIncrement());
        }
        applications.put(application.getId(), application);
        return application;
    }

    public Optional<GrantApplication> findApplicationById(Long id) {
        return Optional.ofNullable(applications.get(id));
    }

    public List<GrantApplication> findAllApplications() {
        return new ArrayList<>(applications.values());
    }

    public boolean deleteApplication(Long applicationId) {
        return applications.remove(applicationId) != null;
    }

    // --- Оценки ---

    public ExpertRating saveRating(ExpertRating rating) {
        if (rating.getId() == null) {
            rating.setId(ratingIdCounter.getAndIncrement());
        }
        ratings.put(rating.getId(), rating);
        return rating;
    }

    public Optional<ExpertRating> findRatingById(Long id) {
        return Optional.ofNullable(ratings.get(id));
    }

    public List<ExpertRating> findRatingsByExpertId(Long expertId) {
        return ratings.values().stream()
                .filter(rating -> rating.getExpertId().equals(expertId))
                .collect(Collectors.toList());
    }

    public List<ExpertRating> findRatingsByApplicationId(Long applicationId) {
        return ratings.values().stream()
                .filter(rating -> rating.getApplicationId().equals(applicationId))
                .collect(Collectors.toList());
    }

    public List<ExpertRating> findAllRatings() {
        return new ArrayList<>(ratings.values());
    }

    public Optional<ExpertRating> findRatingByExpertAndApplication(Long expertId, Long applicationId) {
        return ratings.values().stream()
                .filter(r -> Objects.equals(r.getExpertId(), expertId) && Objects.equals(r.getApplicationId(), applicationId))
                .findFirst();
    }

    public double getAverageRatingForApplication(Long applicationId) {
        List<ExpertRating> list = findRatingsByApplicationId(applicationId);
        if (list.isEmpty()) return 0.0;
        double sum = list.stream().mapToInt(ExpertRating::getScore).sum();
        return sum / list.size();
    }

    public int getRatingCountForApplication(Long applicationId) {
        return findRatingsByApplicationId(applicationId).size();
    }

    public boolean updateRating(Long ratingId, Integer newScore) {
        ExpertRating rating = ratings.get(ratingId);
        if (rating == null) return false;
        rating.setScore(newScore);
        ratings.put(rating.getId(), rating);
        return true;
    }

    public boolean deleteRating(Long ratingId) {
        return ratings.remove(ratingId) != null;
    }

    // --- Утилиты ---

    public void cleanupExpiredTokens() {
        users.values().forEach(user -> {
            if (user.getToken() != null && !user.isTokenValid()) {
                user.logout();
            }
        });
    }
}
