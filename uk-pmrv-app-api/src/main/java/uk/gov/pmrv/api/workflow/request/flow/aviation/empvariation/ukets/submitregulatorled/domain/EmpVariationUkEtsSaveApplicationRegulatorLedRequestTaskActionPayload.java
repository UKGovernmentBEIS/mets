package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsSaveApplicationRegulatorLedAbstractRequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload
		extends EmpVariationUkEtsSaveApplicationRegulatorLedAbstractRequestTaskActionPayload {

	private EmpVariationUkEtsRegulatorLedReason reasonRegulatorLed;

}
