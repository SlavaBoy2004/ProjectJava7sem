package academy.tochkavhoda.competition.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateApplicationResponseDto {
    private Long ratingId;
    private Long applicationId;
    private Integer score;
}
