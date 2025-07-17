package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WithholdingOfAllowancesRequestPayload extends RequestPayload {

    private WithholdingOfAllowances withholdingOfAllowances;

    private WithholdingWithdrawal withholdingWithdrawal;

    @Builder.Default
    private Map<String, Boolean> withholdingOfAllowancesSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> withholdingOfAllowancesWithdrawalSectionsCompleted = new HashMap<>();

    private FileInfoDTO withholdingOfAllowancesOfficialNotice;

    private FileInfoDTO withholdingOfAllowancesWithdrawnOfficialNotice;

    private DecisionNotification decisionNotification;

    private DecisionNotification withdrawDecisionNotification;

    private WithholdingOfAllowancesWithdrawalCloseJustification closeJustification;

    @Builder.Default
    private Map<UUID, String> withholdingOfAllowancesWithdrawalAttachments = new HashMap<>();
}
