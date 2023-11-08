package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload extends RequestTaskPayload {

    private WithholdingWithdrawal withholdingWithdrawal;

    private WithholdingOfAllowancesWithdrawalCloseJustification closeJustification;

    @Builder.Default
    private Map<UUID, String> withholdingWithdrawalAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> sectionsCompleted = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getWithholdingWithdrawalAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return this.closeJustification != null ? this.closeJustification.getFiles() : Collections.emptySet();
    }
}
