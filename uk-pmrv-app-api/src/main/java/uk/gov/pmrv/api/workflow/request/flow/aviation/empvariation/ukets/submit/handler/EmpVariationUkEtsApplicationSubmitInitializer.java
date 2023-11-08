package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.handler;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsApplicationSubmitInitializer implements InitializeRequestTaskHandler {

	private final EmissionsMonitoringPlanQueryService empQueryService;
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	private final EmpVariationUkEtsMapper empVariationUkEtsMapper = Mappers.getMapper(EmpVariationUkEtsMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		final EmissionsMonitoringPlanUkEtsContainer empContainer = 
    			empQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(request.getAccountId())
    			.map(EmissionsMonitoringPlanUkEtsDTO::getEmpContainer)
    			.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));		
		final RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
		
		final EmissionsMonitoringPlanUkEts emp = empVariationUkEtsMapper.cloneEmissionsMonitoringPlanUkEts(
				empContainer.getEmissionsMonitoringPlan(), accountInfo.getOperatorName(), accountInfo.getCrcoCode());
		
		return EmpVariationUkEtsApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                .emissionsMonitoringPlan(emp)
                .serviceContactDetails(accountInfo.getServiceContactDetails())
                .empAttachments(empContainer.getEmpAttachments())
                .build();
	}

	@Override
	public Set<RequestTaskType> getRequestTaskTypes() {
		return Set.of(RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT);
	}
	
}
