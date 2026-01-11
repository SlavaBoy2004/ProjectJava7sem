package academy.tochkavhoda.competition.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteRatingRequestDto {
    private Long applicationId; // удаляем свою оценку по заявке
}
