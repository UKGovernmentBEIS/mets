package uk.gov.pmrv.api.migration.notes.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
