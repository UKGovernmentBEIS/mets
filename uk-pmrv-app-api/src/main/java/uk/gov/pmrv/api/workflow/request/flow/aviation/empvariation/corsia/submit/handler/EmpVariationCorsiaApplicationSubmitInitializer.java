package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.handler;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaApplicationSubmitInitializer implements InitializeRequestTaskHandler {

	private final EmissionsMonitoringPlanQueryService empQueryService;
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	private final EmpVariationCorsiaMapper empVariationCorsiaMapper = 
			Mappers.getMapper(EmpVariationCorsiaMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		final EmissionsMonitoringPlanCorsiaContainer empContainer = 
    			empQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(request.getAccountId())
    			.map(EmissionsMonitoringPlanCorsiaDTO::getEmpContainer)
    			.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));	
		final RequestAviationAccountInfo accountInfo = 
				requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
		
		final EmissionsMonitoringPlanCorsia emp = empVariationCorsiaMapper.cloneEmissionsMonitoringPlanCorsia(
				empContainer.getEmissionsMonitoringPlan(), accountInfo.getOperatorName());
		
		return EmpVariationCorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .emissionsMonitoringPlan(emp)
                .serviceContactDetails(accountInfo.getServiceContactDetails())
                .empAttachments(empContainer.getEmpAttachments())
                .build();
	}

	@Override
	public Set<RequestTaskType> getRequestTaskTypes() {
		return Set.of(RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_SUBMIT);
	}
}
