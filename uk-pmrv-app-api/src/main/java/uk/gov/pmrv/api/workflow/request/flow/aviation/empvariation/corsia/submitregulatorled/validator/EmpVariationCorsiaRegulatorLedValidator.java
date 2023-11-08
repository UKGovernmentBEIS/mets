package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.validator;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.validator.EmpVariationCorsiaDetailsValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaRegulatorLedValidator {

	private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empValidatorService;
	private final EmpVariationCorsiaDetailsValidator empVariationDetailsValidator;
	private static final EmpVariationCorsiaMapper MAPPER = Mappers.getMapper(EmpVariationCorsiaMapper.class);

	public void validateEmp(final EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload) {

		final EmissionsMonitoringPlanCorsiaContainer empContainer = 
			MAPPER.toEmissionsMonitoringPlanCorsiaContainer(requestTaskPayload);
		
		empValidatorService.validateEmissionsMonitoringPlan(empContainer);
		empVariationDetailsValidator.validate(requestTaskPayload.getEmpVariationDetails());
	}
}
