package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.ExpertRatingDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.model.ExpertRating;
import java.util.List;
import java.util.Optional;

public class ExpertRatingDaoImpl implements ExpertRatingDao {
    private Database database = Database.getInstance();

    @Override
    public Optional<ExpertRating> findById(Long id) {
        return database.findRatingById(id);
    }

    @Override
    public List<ExpertRating> findAll() {
        return database.findAllRatings();
    }

    @Override
    public List<ExpertRating> findByExpertId(Long expertId) {
        return database.findRatingsByExpertId(expertId);
    }

    @Override
    public List<ExpertRating> findByApplicationId(Long applicationId) {
        return database.findRatingsByApplicationId(applicationId);
    }

    @Override
    public Optional<ExpertRating> findByExpertAndApplication(Long expertId, Long applicationId) {
        return database.findRatingByExpertAndApplication(expertId, applicationId);
    }

    @Override
    public Double getAverageRatingForApplication(Long applicationId) {
        return database.getAverageRatingForApplication(applicationId);
    }

    @Override
    public int getRatingCountForApplication(Long applicationId) {
        return database.getRatingCountForApplication(applicationId);
    }

    @Override
    public ExpertRating save(ExpertRating rating) {
        return database.saveRating(rating);
    }

    @Override
    public boolean delete(Long id) {
        return database.deleteRating(id);
    }

    @Override
    public boolean updateRating(Long ratingId, Integer newScore) {
        return database.updateRating(ratingId, newScore);
    }
}