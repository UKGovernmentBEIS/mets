package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper.AviationAerCorsiaReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;

import java.util.Set;

@Service
public class AviationAerCorsiaApplicationReviewInitializer extends AviationAerCorsiaReviewInitializer {

	public AviationAerCorsiaApplicationReviewInitializer(
			RequestAviationAccountQueryService requestAviationAccountQueryService,
			RequestVerificationService requestVerificationService,
			AviationAerCorsiaReviewMapper aviationAerCorsiaReviewMapper) {
		super(requestAviationAccountQueryService, requestVerificationService, aviationAerCorsiaReviewMapper);
	}

    @Override
    protected RequestTaskPayloadType getRequestTaskPayloadType() {
        return RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_REVIEW_PAYLOAD;
    }


    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_REVIEW);
    }
}
