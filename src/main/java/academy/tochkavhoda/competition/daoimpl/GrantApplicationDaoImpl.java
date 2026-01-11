package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.GrantApplicationDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.model.GrantApplication;
import java.util.List;
import java.util.Optional;

public class GrantApplicationDaoImpl implements GrantApplicationDao {
    private Database database = Database.getInstance();

    @Override
    public Optional<GrantApplication> findById(Long id) {
        return database.findApplicationById(id);
    }

    @Override
    public List<GrantApplication> findAll() {
        return database.findAllApplications();
    }

    @Override
    public List<GrantApplication> findByParticipantId(Long participantId) {
        return database.findApplicationsByParticipantId(participantId);
    }

    @Override
    public List<GrantApplication> findByDirections(List<String> directions) {
        return database.findApplicationsByDirections(directions);
    }

    @Override
    public List<GrantApplication> findActiveApplications() {
        return database.findActiveApplications();
    }

    @Override
    public GrantApplication save(GrantApplication application) {
        return database.saveApplication(application);
    }

    @Override
    public boolean delete(Long id) {
        return database.deleteApplication(id);
    }

    @Override
    public boolean deactivate(Long id) {
        return database.deactivateApplication(id);
    }
}