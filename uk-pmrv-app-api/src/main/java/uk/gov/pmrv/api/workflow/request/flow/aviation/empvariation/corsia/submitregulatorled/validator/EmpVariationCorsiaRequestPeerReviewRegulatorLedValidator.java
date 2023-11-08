package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaRequestPeerReviewRegulatorLedValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final EmpVariationCorsiaRegulatorLedValidator empValidator;

    public void validate(final RequestTask requestTask,
                         final PeerReviewRequestTaskActionPayload payload,
                         final PmrvUser pmrvUser) {

        peerReviewerTaskAssignmentValidator.validate(
            RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW, payload.getPeerReviewer(),
            pmrvUser
        );

        final EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();

        empValidator.validateEmp(taskPayload);
    }
}
