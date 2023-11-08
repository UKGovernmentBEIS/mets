package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.mapper.EmpUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

@Service
@RequiredArgsConstructor
public class EmpIssuanceUkEtsApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private static final EmpUkEtsReviewMapper EMP_UKETS_REVIEW_MAPPER = Mappers.getMapper(EmpUkEtsReviewMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		
		RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        return EMP_UKETS_REVIEW_MAPPER.toEmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload(
            (EmpIssuanceUkEtsRequestPayload)request.getPayload(),
            aviationAccountInfo,
            RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD
        );
	}

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT);
    }
}
