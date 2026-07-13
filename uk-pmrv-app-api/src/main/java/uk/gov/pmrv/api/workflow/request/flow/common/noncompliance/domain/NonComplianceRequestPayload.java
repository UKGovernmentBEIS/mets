package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class NonComplianceRequestPayload extends RequestPayload {

    @NotNull
    private NonComplianceReason reason;

    private LocalDate nonComplianceDate;

    private LocalDate complianceDate;

    @Size(max = 10000)
    private String comments;

    private NonComplianceCloseJustification closeJustification;
    
    private UUID dailyPenaltyNotice;

    private String dailyPenaltyComments;
    
    private Boolean dailyPenaltyCompleted;
    
    private Boolean issueNoticeOfIntent;

    private UUID noticeOfIntent;

    private String noticeOfIntentComments;

    private Boolean noticeOfIntentCompleted;

    private UUID civilPenalty;

    @Size(max = 255)
    private String civilPenaltyAmount;

    private LocalDate civilPenaltyDueDate;

    @Size(max = 10000)
    private String civilPenaltyComments;

    private Boolean civilPenaltyCompleted;
    
    private boolean reIssueCivilPenalty;

    @Builder.Default
    private Map<UUID, String> nonComplianceAttachments = new HashMap<>();
}
