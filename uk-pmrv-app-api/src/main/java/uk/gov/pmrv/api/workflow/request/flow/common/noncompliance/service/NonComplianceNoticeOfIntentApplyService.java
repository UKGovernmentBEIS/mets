package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@Service
@RequiredArgsConstructor
public class NonComplianceNoticeOfIntentApplyService {

    public void applySaveAction(final RequestTask requestTask,
                                final NonComplianceNoticeOfIntentSaveApplicationRequestTaskActionPayload taskActionPayload) {

        final NonComplianceNoticeOfIntentRequestTaskPayload
            requestTaskPayload = (NonComplianceNoticeOfIntentRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setNoticeOfIntent(taskActionPayload.getNoticeOfIntent());
        requestTaskPayload.setComments(taskActionPayload.getComments());
        requestTaskPayload.setNoticeOfIntentCompleted(taskActionPayload.getNoticeOfIntentCompleted());
    }

    public void saveRequestPeerReviewAction(final RequestTask requestTask,
                                            final String peerReviewer) {

        final NonComplianceNoticeOfIntentRequestTaskPayload taskPayload =
            (NonComplianceNoticeOfIntentRequestTaskPayload) requestTask.getPayload();
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) requestTask.getRequest().getPayload();

        requestPayload.setNoticeOfIntent(taskPayload.getNoticeOfIntent());
        requestPayload.setNoticeOfIntentComments(taskPayload.getComments());
        requestPayload.setNonComplianceAttachments(taskPayload.getNonComplianceAttachments());
        requestPayload.setNoticeOfIntentCompleted(taskPayload.getNoticeOfIntentCompleted());

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
    }
}
