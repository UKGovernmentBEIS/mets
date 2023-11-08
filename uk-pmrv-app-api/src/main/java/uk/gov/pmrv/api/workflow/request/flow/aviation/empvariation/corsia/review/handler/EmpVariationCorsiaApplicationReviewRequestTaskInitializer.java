package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.handler;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.utils.EmpCorsiaReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.mapper.EmpVariationCorsiaReviewMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaApplicationReviewRequestTaskInitializer implements InitializeRequestTaskHandler {

	private final EmissionsMonitoringPlanQueryService empQueryService;
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	private static final EmpVariationCorsiaReviewMapper MAPPER = Mappers.getMapper(EmpVariationCorsiaReviewMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		
		final EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
		final EmissionsMonitoringPlanCorsiaContainer originalEmpContainer = 
    			empQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(request.getAccountId())
    			.map(EmissionsMonitoringPlanCorsiaDTO::getEmpContainer)
    			.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));		
		final RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
		final EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = 
				MAPPER.toEmpVariationCorsiaApplicationReviewRequestTaskPayload(
						requestPayload, accountInfo, RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD);
		
		requestTaskPayload.setOriginalEmpContainer(originalEmpContainer);
		
		if (reviewGroupDecisionsNotYetSet(requestPayload)) {
            requestTaskPayload.setReviewGroupDecisions(EmpCorsiaReviewUtils.getEmpCorsiaReviewGroups(requestPayload.getEmissionsMonitoringPlan())
                .stream()
                .collect(Collectors.toMap(Function.identity(), reviewGroup -> EmpVariationReviewDecision
                		.builder()
                		.type(EmpVariationReviewDecisionType.ACCEPTED)
                		.details(EmpAcceptedVariationDecisionDetails
                				.builder()
                				.build())
                		.build())));
            
            requestTaskPayload.setEmpVariationDetailsReviewDecision(EmpVariationReviewDecision
            		.builder()
            		.type(EmpVariationReviewDecisionType.ACCEPTED)
            		.details(EmpAcceptedVariationDecisionDetails
            				.builder()
            				.build())
            		.build());
        }
		
		return requestTaskPayload;
	}

	@Override
	public Set<RequestTaskType> getRequestTaskTypes() {
		return Set.of(RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW);
	}
	
	private static boolean reviewGroupDecisionsNotYetSet(EmpVariationCorsiaRequestPayload requestPayload) {
        return requestPayload.getReviewGroupDecisions() == null || 
        		requestPayload.getReviewGroupDecisions().isEmpty() || 
        		requestPayload.getEmpVariationDetailsReviewDecision() == null;
    }
}
