package academy.tochkavhoda.competition.dto.response;

import academy.tochkavhoda.competition.model.GrantApplication;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMyGrantApplicationsResponseDto {
    private List<GrantApplication> applications;
}
