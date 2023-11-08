package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.handler;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.mapper.EmpVariationUkEtsReviewMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsWaitForAmendsInitializer implements InitializeRequestTaskHandler {

	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private static final EmpVariationUkEtsReviewMapper MAPPER = Mappers.getMapper(EmpVariationUkEtsReviewMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		
		RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        return MAPPER.toEmpVariationUkEtsApplicationReviewRequestTaskPayload(
            (EmpVariationUkEtsRequestPayload)request.getPayload(),
            aviationAccountInfo,
            RequestTaskPayloadType.EMP_VARIATION_UKETS_WAIT_FOR_AMENDS_PAYLOAD
        );
	}

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_VARIATION_UKETS_WAIT_FOR_AMENDS);
    }
}
