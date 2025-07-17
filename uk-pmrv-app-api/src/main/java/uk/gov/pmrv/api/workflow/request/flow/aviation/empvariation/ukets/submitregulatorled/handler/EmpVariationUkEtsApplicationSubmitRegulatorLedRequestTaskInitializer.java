package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.handler;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.mapper.EmpVariationUkEtsSubmitRegulatorLedMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskInitializer implements InitializeRequestTaskHandler {

	private final EmissionsMonitoringPlanQueryService empQueryService;
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	private final EmpVariationUkEtsMapper empVariationUkEtsMapper = Mappers.getMapper(EmpVariationUkEtsMapper.class);
	private final EmpVariationUkEtsSubmitRegulatorLedMapper empVariationUkEtsRegulatorLedMapper = Mappers.getMapper(EmpVariationUkEtsSubmitRegulatorLedMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
		
		final EmissionsMonitoringPlanUkEtsContainer originalEmpContainer = 
    			empQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(request.getAccountId())
    			.map(EmissionsMonitoringPlanUkEtsDTO::getEmpContainer)
    			.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
		final RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
		
		final EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload;
		if(isAlreadyDetermined(requestPayload)) {
			requestTaskPayload = empVariationUkEtsRegulatorLedMapper
					.toEmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload(requestPayload, accountInfo,
							RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD);
		} else {
			final EmissionsMonitoringPlanUkEts emp = empVariationUkEtsMapper.cloneEmissionsMonitoringPlanUkEts(
					originalEmpContainer.getEmissionsMonitoringPlan(), accountInfo.getOperatorName(),
					accountInfo.getCrcoCode());

			requestTaskPayload = EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.builder()
	                .payloadType(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD)
	                .originalEmpContainer(originalEmpContainer)
	                .emissionsMonitoringPlan(emp)
	                .serviceContactDetails(accountInfo.getServiceContactDetails())
	                .empAttachments(originalEmpContainer.getEmpAttachments())
	                .build();
		}
		
		return requestTaskPayload;
	}

	@Override
	public Set<RequestTaskType> getRequestTaskTypes() {
		return Set.of(RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT);
	}
	
	private boolean isAlreadyDetermined(EmpVariationUkEtsRequestPayload requestPayload) {
		return requestPayload.getReasonRegulatorLed() != null;
	}
	
}