package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsRegulatorLedPeerReviewService {

	@Transactional
    public void saveRequestPeerReviewAction(final RequestTask requestTask,
                                            final String selectedPeerReviewer,
                                            final String appUserId) {
        final EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();

        final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) requestTask.getRequest().getPayload();

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
