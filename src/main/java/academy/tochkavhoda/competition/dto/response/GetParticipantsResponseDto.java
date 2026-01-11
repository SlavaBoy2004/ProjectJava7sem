package academy.tochkavhoda.competition.dto.response;

import academy.tochkavhoda.competition.model.Participant;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetParticipantsResponseDto {
    private List<Participant> participants;
}
