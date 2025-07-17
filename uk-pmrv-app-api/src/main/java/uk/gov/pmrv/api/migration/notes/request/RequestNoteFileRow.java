package uk.gov.pmrv.api.migration.notes.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestNoteFileRow {
    
    private String noteId;
    private String requestId;
    private UUID attachmentId;
    private String fileContent;
    private String fileName;
}
