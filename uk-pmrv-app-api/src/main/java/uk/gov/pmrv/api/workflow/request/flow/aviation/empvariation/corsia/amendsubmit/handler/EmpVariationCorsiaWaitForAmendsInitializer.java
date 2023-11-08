package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.mapper.EmpVariationCorsiaReviewMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaWaitForAmendsInitializer implements InitializeRequestTaskHandler {

	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private static final EmpVariationCorsiaReviewMapper MAPPER = Mappers.getMapper(EmpVariationCorsiaReviewMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		
		RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        return MAPPER.toEmpVariationCorsiaApplicationReviewRequestTaskPayload(
            (EmpVariationCorsiaRequestPayload)request.getPayload(),
            aviationAccountInfo,
            RequestTaskPayloadType.EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS_PAYLOAD
        );
	}

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS);
    }
}
