package academy.tochkavhoda.competition.model;

import lombok.*;
import java.util.List;

@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class GrantApplication {
    private Long id;
    private String title;
    private String description;
    private List<String> directions;
    private Double requestedAmount;
    private Long participantId; // ID участника, подавшего заявку
    private boolean isActive;
}
