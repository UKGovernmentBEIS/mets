package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationVirSaveRespondToRegulatorCommentsRequestTaskActionPayload extends
    AviationVirRespondToRegulatorCommentsRequestTaskActionPayload {

    @Valid
    @NotNull
    private OperatorImprovementFollowUpResponse operatorImprovementFollowUpResponse;
}
