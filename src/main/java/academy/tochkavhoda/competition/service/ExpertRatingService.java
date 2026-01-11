package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ExpertRatingDao;
import academy.tochkavhoda.competition.dao.GrantApplicationDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.dto.request.DeleteRatingRequestDto;
import academy.tochkavhoda.competition.dto.request.RateApplicationRequestDto;
import academy.tochkavhoda.competition.dto.response.GetMyRatingsResponseDto;
import academy.tochkavhoda.competition.dto.response.RateApplicationResponseDto;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.ExpertRating;
import academy.tochkavhoda.competition.model.GrantApplication;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;
import com.google.gson.Gson;

import java.util.List;

public class ExpertRatingService {

    private final UserDao userDao;
    private final GrantApplicationDao applicationDao;
    private final ExpertRatingDao ratingDao;
    private final Gson gson = new Gson();

    public ExpertRatingService(UserDao userDao, GrantApplicationDao applicationDao, ExpertRatingDao ratingDao) {
        this.userDao = userDao;
        this.applicationDao = applicationDao;
        this.ratingDao = ratingDao;
    }

    // поставить или обновить оценку
    public ServerResponse rateApplication(String token, String requestJsonString) {
        try {
            User user = userDao.findByToken(token).orElse(null);
            if (user == null) return ServerResponse.error("Invalid token");
            if (!user.isExpert() || !user.canUseSystem()) return ServerResponse.error("Access denied");

            RateApplicationRequestDto req = gson.fromJson(requestJsonString, RateApplicationRequestDto.class);
            if (req == null) return ServerResponse.error("Некорректный JSON");
            if (req.getApplicationId() == null) return ServerResponse.error("ApplicationId is empty");
            if (req.getScore() == null || req.getScore() < 1 || req.getScore() > 5) return ServerResponse.error("Score must be 1..5");

            Expert expert = (Expert) user;

            GrantApplication app = applicationDao.findById(req.getApplicationId()).orElse(null);
            if (app == null) return ServerResponse.error("Application not found");
            if (!app.isActive()) return ServerResponse.error("Application is not active");

            // эксперт может оценивать только по своим направлениям
            boolean okDirection = app.getDirections() != null &&
                    app.getDirections().stream().anyMatch(expert.getExpertDirections()::contains);
            if (!okDirection) return ServerResponse.error("Expert cannot rate this application directions");

            ExpertRating existing = ratingDao.findByExpertAndApplication(expert.getId(), app.getId()).orElse(null);

            ExpertRating saved;
            if (existing == null) {
                ExpertRating rating = ExpertRating.builder()
                        .expertId(expert.getId())
                        .applicationId(app.getId())
                        .score(req.getScore())
                        .build();
                saved = ratingDao.save(rating);
            } else {
                boolean updated = ratingDao.updateRating(existing.getId(), req.getScore());
                if (!updated) return ServerResponse.error("Cannot update rating");
                saved = ratingDao.findById(existing.getId()).orElse(existing);
            }

            String json = gson.toJson(RateApplicationResponseDto.builder()
                    .ratingId(saved.getId())
                    .applicationId(saved.getApplicationId())
                    .score(saved.getScore())
                    .build());

            return ServerResponse.ok(json);

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("Bad request");
        }
    }

    public ServerResponse deleteMyRating(String token, String requestJsonString) {
        try {
            User user = userDao.findByToken(token).orElse(null);
            if (user == null) return ServerResponse.error("Invalid token");
            if (!user.isExpert() || !user.canUseSystem()) return ServerResponse.error("Access denied");

            DeleteRatingRequestDto req = gson.fromJson(requestJsonString, DeleteRatingRequestDto.class);
            if (req == null) return ServerResponse.error("Некорректный JSON");
            if (req.getApplicationId() == null) return ServerResponse.error("ApplicationId is empty");

            Expert expert = (Expert) user;

            ExpertRating existing = ratingDao.findByExpertAndApplication(expert.getId(), req.getApplicationId()).orElse(null);
            if (existing == null) return ServerResponse.error("Rating not found");

            boolean ok = ratingDao.delete(existing.getId());
            return ok ? ServerResponse.ok("{}") : ServerResponse.error("Cannot delete rating");

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("Bad request");
        }
    }

    public ServerResponse getMyRatings(String token, String requestJsonString) {
        try {
            User user = userDao.findByToken(token).orElse(null);
            if (user == null) return ServerResponse.error("Invalid token");
            if (!user.isExpert()) return ServerResponse.error("Access denied");

            Expert expert = (Expert) user;

            List<ExpertRating> list = ratingDao.findByExpertId(expert.getId());
            String json = gson.toJson(GetMyRatingsResponseDto.builder().ratings(list).build());
            return ServerResponse.ok(json);

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error("Bad request");
        }
    }
}
