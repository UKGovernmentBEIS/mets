package uk.gov.pmrv.api.account.fileattachment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "account_file_attachment")
public class AccountFileAttachment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "account_file_attachment_seq_gen"
    )
    @SequenceGenerator(
            name = "account_file_attachment_seq_gen",
            sequenceName = "account_file_attachment_seq",
            allocationSize = 1
    )
    private Long id;

    /**
     * Main workflow name, e.g. "ALR", "WASTE_QDR", "AER"
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "workflow", nullable = false)
    @EqualsAndHashCode.Include()
    private AccountFileAttachmentWorkflow workflow;

    /**
     * Optional subtype inside the workflow, e.g. ALR_ATTACHMENT, ALR_VOS.
     * Can be null for workflows without subtypes.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "workflow_subtype")
    @EqualsAndHashCode.Include()
    private AccountFileAttachmentWorkflowSubType workflowSubtype;

    /**
     * METS request id.
     */
    @NotNull
    @Column(name = "originated_request_id", nullable = false, length = 20)
    private String originatedRequestId;

    /**
     * Status: "IN_PROGRESS", "FINALIZED"
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status", nullable = false)
    private AccountFileAttachmentStatus status;

    /**
     * METS account id.
     */
    @NotNull
    @Column(name = "account_id", nullable = false)
    @EqualsAndHashCode.Include()
    private Long accountId;

    /**
     * Period: "2025", "2024", "2025 Q1" etc.
     */
    @NotNull
    @Column(name = "period", nullable = false)
    @EqualsAndHashCode.Include()
    private String period;

    /**
     * UUID from FileAttachmentService / file_attachment table.
     */
    @NotNull
    @Column(name = "file_uuid", nullable = false)
    private String fileUuid;

    /**
     * Competent authority code, e.g. "ENGLAND".
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "competent_authority", nullable = false)
    private CompetentAuthorityEnum competentAuthority;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;
}
