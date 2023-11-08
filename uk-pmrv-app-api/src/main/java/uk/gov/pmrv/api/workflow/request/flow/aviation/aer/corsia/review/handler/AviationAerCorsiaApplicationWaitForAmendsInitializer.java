package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper.AviationAerCorsiaReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;

import java.util.Set;

@Service
public class AviationAerCorsiaApplicationWaitForAmendsInitializer extends AviationAerCorsiaReviewInitializer {
    public AviationAerCorsiaApplicationWaitForAmendsInitializer(RequestAviationAccountQueryService requestAviationAccountQueryService,
                                                                RequestVerificationService<AviationAerCorsiaVerificationReport> requestVerificationService,
                                                                AviationAerCorsiaReviewMapper aviationAerCorsiaReviewMapper) {
        super(requestAviationAccountQueryService, requestVerificationService, aviationAerCorsiaReviewMapper);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_CORSIA_WAIT_FOR_AMENDS);
    }

    @Override
    protected RequestTaskPayloadType getRequestTaskPayloadType() {
        return RequestTaskPayloadType.AVIATION_AER_CORSIA_WAIT_FOR_AMENDS_PAYLOAD;
    }
}
