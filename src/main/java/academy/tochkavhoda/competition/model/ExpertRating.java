package academy.tochkavhoda.competition.model;

import lombok.*;

@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ExpertRating {
    private Long id;
    private Long expertId;
    private Long applicationId;
    private Integer score; // 1-5
}

