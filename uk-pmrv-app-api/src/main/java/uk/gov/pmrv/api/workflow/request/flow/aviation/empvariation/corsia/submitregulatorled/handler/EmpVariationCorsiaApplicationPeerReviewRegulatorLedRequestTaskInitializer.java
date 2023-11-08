package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.mapper.EmpVariationCorsiaSubmitRegulatorLedMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaApplicationPeerReviewRegulatorLedRequestTaskInitializer implements InitializeRequestTaskHandler {
	
	private static final EmpVariationCorsiaSubmitRegulatorLedMapper MAPPER = 
		Mappers.getMapper(EmpVariationCorsiaSubmitRegulatorLedMapper.class);
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	
	@Override
	public RequestTaskPayload initializePayload(final Request request) {
		
		final EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
        final RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        
        return MAPPER.toEmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload(
                requestPayload, accountInfo, RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD
            );
	}

	@Override
	public Set<RequestTaskType> getRequestTaskTypes() {
		return Set.of(RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW);
	}

}
