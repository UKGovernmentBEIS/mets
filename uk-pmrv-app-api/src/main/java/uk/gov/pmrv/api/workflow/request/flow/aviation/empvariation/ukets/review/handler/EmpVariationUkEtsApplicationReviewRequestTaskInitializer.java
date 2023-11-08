package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.utils.EmpUkEtsReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.mapper.EmpVariationUkEtsReviewMapper;


@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsApplicationReviewRequestTaskInitializer implements InitializeRequestTaskHandler {

	private final EmissionsMonitoringPlanQueryService empQueryService;
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	private static final EmpVariationUkEtsReviewMapper EMP_VARIATION_UKETS_MAPPER = Mappers.getMapper(EmpVariationUkEtsReviewMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		
		final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
		final EmissionsMonitoringPlanUkEtsContainer originalEmpContainer = 
    			empQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(request.getAccountId())
    			.map(EmissionsMonitoringPlanUkEtsDTO::getEmpContainer)
    			.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));		
		final RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
		final EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EMP_VARIATION_UKETS_MAPPER
				.toEmpVariationUkEtsApplicationReviewRequestTaskPayload(
						requestPayload, accountInfo, RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_REVIEW_PAYLOAD);
		
		requestTaskPayload.setOriginalEmpContainer(originalEmpContainer);
		
		if (reviewGroupDecisionsNotYetSet(requestPayload)) {
            requestTaskPayload.setReviewGroupDecisions(EmpUkEtsReviewUtils.getEmpUkEtsReviewGroups(requestPayload.getEmissionsMonitoringPlan())
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
		return Set.of(RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW);
	}
	
	private static boolean reviewGroupDecisionsNotYetSet(EmpVariationUkEtsRequestPayload requestPayload) {
        return requestPayload.getReviewGroupDecisions() == null || 
        		requestPayload.getReviewGroupDecisions().isEmpty() || 
        		requestPayload.getEmpVariationDetailsReviewDecision() == null;
    }
}
