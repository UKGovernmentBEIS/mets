package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaRegulatorLedPeerReviewService {

    @Transactional
    public void saveRequestPeerReviewAction(final RequestTask requestTask,
                                            final String selectedPeerReviewer,
                                            final String appUserId) {

        final EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();

        final EmpVariationCorsiaRequestPayload requestPayload =
            (EmpVariationCorsiaRequestPayload) requestTask.getRequest().getPayload();

        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);

        requestPayload.setRegulatorReviewer(appUserId);
        requestPayload.setEmissionsMonitoringPlan(taskPayload.getEmissionsMonitoringPlan());
        requestPayload.setEmpVariationDetails(taskPayload.getEmpVariationDetails());
        requestPayload.setEmpAttachments(taskPayload.getEmpAttachments());
        requestPayload.setEmpSectionsCompleted(taskPayload.getEmpSectionsCompleted());
        requestPayload.setEmpVariationDetailsCompleted(taskPayload.getEmpVariationDetailsCompleted());
        requestPayload.setReviewGroupDecisionsRegulatorLed(taskPayload.getReviewGroupDecisions());
        requestPayload.setReasonRegulatorLed(taskPayload.getReasonRegulatorLed());
    }
}
