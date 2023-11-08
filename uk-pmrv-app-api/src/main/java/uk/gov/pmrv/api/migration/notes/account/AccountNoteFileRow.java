package uk.gov.pmrv.api.migration.notes.account;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountNoteFileRow {
    
    private String noteId;
    private Long accountId;
    private UUID attachmentId;
    private String fileContent;
    private String fileName;
}
