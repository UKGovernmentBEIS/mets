package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

@Service
public class EmpIssuanceCorsiaApplicationReviewInitializer extends EmpIssuanceCorsiaReviewInitializer {


    public EmpIssuanceCorsiaApplicationReviewInitializer(RequestAviationAccountQueryService requestAviationAccountQueryService) {
        super(requestAviationAccountQueryService);
    }

    @Override
    protected RequestTaskPayloadType getRequestTaskPayloadType() {
        return RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW);
    }
}
