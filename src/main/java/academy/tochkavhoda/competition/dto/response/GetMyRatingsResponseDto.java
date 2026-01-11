package academy.tochkavhoda.competition.dto.response;

import academy.tochkavhoda.competition.model.ExpertRating;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMyRatingsResponseDto {
    private List<ExpertRating> ratings;
}
