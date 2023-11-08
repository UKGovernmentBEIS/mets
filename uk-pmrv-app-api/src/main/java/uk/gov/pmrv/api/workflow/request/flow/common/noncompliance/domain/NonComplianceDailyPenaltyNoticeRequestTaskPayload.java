package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceDailyPenaltyNoticeRequestTaskPayload extends RequestTaskPayload implements NonComplianceRequestTaskClosable {

    @NotNull
    private Boolean issueNoticeOfIntent; // read only for UI purposes
    
    @NotNull
    private UUID dailyPenaltyNotice;

    @Size(max = 10000)
    private String comments;
    
    private NonComplianceCloseJustification closeJustification;
    
    @Builder.Default
    private Map<UUID, String> nonComplianceAttachments = new HashMap<>();

    private Boolean dailyPenaltyCompleted;

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getNonComplianceAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return this.closeJustification != null ? this.closeJustification.getFiles() : Set.of(this.dailyPenaltyNotice);
    }
}
