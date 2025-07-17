package uk.gov.pmrv.api.migration.notes.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestNoteRow {
    
    private String noteId;
    private String requestId;
    private LocalDateTime dateCreated;
    private String submitter;
    private String payload;
}
