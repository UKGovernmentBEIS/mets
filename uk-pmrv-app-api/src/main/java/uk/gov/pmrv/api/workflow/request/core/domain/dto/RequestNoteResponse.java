package uk.gov.pmrv.api.workflow.request.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestNoteResponse {

    private List<RequestNoteDto> requestNotes;
    private Long totalItems;
}
