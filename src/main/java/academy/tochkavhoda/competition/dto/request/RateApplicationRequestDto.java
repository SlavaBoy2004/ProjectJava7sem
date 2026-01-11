package academy.tochkavhoda.competition.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateApplicationRequestDto {
    private Long applicationId;
    private Integer score; // 1..5
}
