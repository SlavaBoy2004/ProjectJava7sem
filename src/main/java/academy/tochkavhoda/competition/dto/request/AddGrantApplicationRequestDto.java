package academy.tochkavhoda.competition.dto.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddGrantApplicationRequestDto {
    private String title;
    private String description;
    private List<String> directions;
    private Double requestedAmount;
}
