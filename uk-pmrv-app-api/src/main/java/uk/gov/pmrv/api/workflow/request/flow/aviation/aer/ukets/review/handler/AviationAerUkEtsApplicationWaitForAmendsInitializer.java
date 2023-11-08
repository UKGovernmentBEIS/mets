package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper.AviationAerUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;

import java.util.Set;

@Service
public class AviationAerUkEtsApplicationWaitForAmendsInitializer extends AviationAerUkEtsReviewInitializer {

    public AviationAerUkEtsApplicationWaitForAmendsInitializer(RequestAviationAccountQueryService requestAviationAccountQueryService,
                                                               RequestVerificationService<AviationAerUkEtsVerificationReport> requestVerificationService,
                                                               AviationAerUkEtsReviewMapper aviationAerUkEtsReviewMapper) {
        super(requestAviationAccountQueryService, requestVerificationService, aviationAerUkEtsReviewMapper);
    }

    @Override
    protected RequestTaskPayloadType getRequestTaskPayloadType() {
        return RequestTaskPayloadType.AVIATION_AER_UKETS_WAIT_FOR_AMENDS_PAYLOAD;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_UKETS_WAIT_FOR_AMENDS);
    }
}
