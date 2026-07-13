package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload extends
    AviationVirRespondToRegulatorCommentsRequestTaskActionPayload {
}
