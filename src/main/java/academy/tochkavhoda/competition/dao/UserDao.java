package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.model.Admin;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByLogin(String login);
    Optional<User> findByToken(String token);
    User save(User user);
    boolean delete(Long userId);
    Optional<User> findById(Long userId);
    List<User> findAll();
    List<Participant> findParticipants();
    List<Expert> findExperts();
    List<Admin> findAdmins();

}