package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@Service
@RequiredArgsConstructor
public class NonComplianceDailyPenaltyNoticeApplyService {
    
    public void applySaveAction(final RequestTask requestTask,
                                final NonComplianceDailyPenaltyNoticeSaveApplicationRequestTaskActionPayload taskActionPayload) {

        final NonComplianceDailyPenaltyNoticeRequestTaskPayload
            requestTaskPayload = (NonComplianceDailyPenaltyNoticeRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setDailyPenaltyNotice(taskActionPayload.getDailyPenaltyNotice());
        requestTaskPayload.setComments(taskActionPayload.getComments());
        requestTaskPayload.setDailyPenaltyCompleted(taskActionPayload.getDailyPenaltyCompleted());
    }

    public void saveRequestPeerReviewAction(final RequestTask requestTask, 
                                            final String peerReviewer) {

        final NonComplianceDailyPenaltyNoticeRequestTaskPayload taskPayload = 
            (NonComplianceDailyPenaltyNoticeRequestTaskPayload) requestTask.getPayload();
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) requestTask.getRequest().getPayload();
        
        requestPayload.setDailyPenaltyNotice(taskPayload.getDailyPenaltyNotice());
        requestPayload.setDailyPenaltyComments(taskPayload.getComments());
        requestPayload.setNonComplianceAttachments(taskPayload.getNonComplianceAttachments());
        requestPayload.setDailyPenaltyCompleted(taskPayload.getDailyPenaltyCompleted());
        
        requestPayload.setRegulatorPeerReviewer(peerReviewer);
    }
}
