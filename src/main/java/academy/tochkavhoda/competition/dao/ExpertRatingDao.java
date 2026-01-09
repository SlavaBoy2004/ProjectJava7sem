package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.model.ExpertRating;
import java.util.List;
import java.util.Optional;

public interface ExpertRatingDao {
    Optional<ExpertRating> findById(Long id);
    List<ExpertRating> findAll();
    List<ExpertRating> findByExpertId(Long expertId);
    List<ExpertRating> findByApplicationId(Long applicationId);
    Optional<ExpertRating> findByExpertAndApplication(Long expertId, Long applicationId);
    Double getAverageRatingForApplication(Long applicationId);
    int getRatingCountForApplication(Long applicationId);
    ExpertRating save(ExpertRating rating);
    boolean delete(Long id);
    boolean updateRating(Long ratingId, Integer newScore);
}