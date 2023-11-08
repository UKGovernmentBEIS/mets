package uk.gov.pmrv.api.workflow.request.core.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestNoteResponse {

    private List<RequestNoteDto> requestNotes;
    private Long totalItems;
}
