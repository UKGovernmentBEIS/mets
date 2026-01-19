package uk.gov.pmrv.api.account.fileattachment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentStatus;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class AccountFileAttachmentDTO {

    private Long id;
    private AccountFileAttachmentWorkflow workflow;
    private AccountFileAttachmentWorkflowSubType workflowSubtype;
    private String originatedRequestId;
    private AccountFileAttachmentStatus status;
    private Long accountId;
    private String period;
    private String fileUuid;
    private CompetentAuthorityEnum competentAuthority;
    private LocalDateTime creationDate;
}
