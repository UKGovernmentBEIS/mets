package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.util.Set;

@Service
public class EmpIssuanceUkEtsApplicationReviewInitializer extends EmpIssuanceUkEtsReviewInitializer {


    public EmpIssuanceUkEtsApplicationReviewInitializer(RequestAviationAccountQueryService requestAviationAccountQueryService) {
        super(requestAviationAccountQueryService);
    }

    @Override
    protected RequestTaskPayloadType getRequestTaskPayloadType() {
        return RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW);
    }
}
