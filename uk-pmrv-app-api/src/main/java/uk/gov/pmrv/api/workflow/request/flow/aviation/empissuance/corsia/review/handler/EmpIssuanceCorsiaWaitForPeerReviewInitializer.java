package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

@Service
public class EmpIssuanceCorsiaWaitForPeerReviewInitializer extends EmpIssuanceCorsiaReviewInitializer {

    public EmpIssuanceCorsiaWaitForPeerReviewInitializer(RequestAviationAccountQueryService requestAviationAccountQueryService) {
        super(requestAviationAccountQueryService);
    }

    @Override
    protected RequestTaskPayloadType getRequestTaskPayloadType() {
        return RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW_PAYLOAD;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW);
    }
}
