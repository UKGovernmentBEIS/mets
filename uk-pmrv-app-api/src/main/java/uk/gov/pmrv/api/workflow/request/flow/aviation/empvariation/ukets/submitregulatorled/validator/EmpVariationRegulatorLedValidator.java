package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.validator;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.validator.EmpVariationUkEtsDetailsValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;

@Service
@RequiredArgsConstructor
class EmpVariationRegulatorLedValidator {

	private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empValidatorService;
	private final EmpVariationUkEtsDetailsValidator empVariationDetailsValidator;
	private static final EmpVariationUkEtsMapper MAPPER = Mappers.getMapper(EmpVariationUkEtsMapper.class);

	public void validateEmp(EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload) {
		empValidatorService
				.validateEmissionsMonitoringPlan(MAPPER.toEmissionsMonitoringPlanUkEtsContainer(requestTaskPayload));
		empVariationDetailsValidator.validate(requestTaskPayload.getEmpVariationDetails());
	}
}
