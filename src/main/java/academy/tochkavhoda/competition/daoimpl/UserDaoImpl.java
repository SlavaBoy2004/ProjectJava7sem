package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.model.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserDaoImpl implements UserDao {
    private Database database = Database.getInstance();

    @Override
    public Optional<User> findByLogin(String login) {
        return database.findUserByLogin(login);
    }

    @Override
    public Optional<User> findByToken(String token) {
        return database.findUserByToken(token);
    }

    @Override
    public User save(User user) {
        return database.saveUser(user);
    }

    @Override
    public boolean delete(Long userId) {
        return database.deleteUser(userId);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return database.findUserById(userId);
    }

    @Override
    public List<User> findAll() {
        return database.findAllUsers();
    }

    @Override
    public List<Participant> findParticipants() {
        return database.findUsersByType(Participant.class);
    }

    @Override
    public List<Expert> findExperts() {
        return database.findUsersByType(Expert.class);
    }

    @Override
    public List<Admin> findAdmins() {
        return database.findUsersByType(Admin.class);
    }
}