package uk.gov.pmrv.api.migration.notes.account;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountNoteRow {
    
    private String noteId;
    private Long accountId;
    private LocalDateTime dateCreated;
    private String submitter;
    private String payload;
}
