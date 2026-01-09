package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.model.GrantApplication;
import java.util.List;
import java.util.Optional;

public interface GrantApplicationDao {
    Optional<GrantApplication> findById(Long id);
    List<GrantApplication> findAll();
    List<GrantApplication> findByParticipantId(Long participantId);
    List<GrantApplication> findByDirections(List<String> directions);
    List<GrantApplication> findActiveApplications();
    GrantApplication save(GrantApplication application);
    boolean delete(Long id);
    boolean deactivate(Long id);
}