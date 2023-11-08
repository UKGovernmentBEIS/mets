package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@Service
@RequiredArgsConstructor
public class NonComplianceCivilPenaltyApplyService {

    public void applySaveAction(final RequestTask requestTask,
                                final NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload taskActionPayload) {

        final NonComplianceCivilPenaltyRequestTaskPayload
            requestTaskPayload = (NonComplianceCivilPenaltyRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setCivilPenalty(taskActionPayload.getCivilPenalty());
        requestTaskPayload.setPenaltyAmount(taskActionPayload.getPenaltyAmount());
        requestTaskPayload.setDueDate(taskActionPayload.getDueDate());
        requestTaskPayload.setComments(taskActionPayload.getComments());
        requestTaskPayload.setCivilPenaltyCompleted(taskActionPayload.getCivilPenaltyCompleted());
    }


    public void saveRequestPeerReviewAction(final RequestTask requestTask,
                                            final String peerReviewer) {

        final NonComplianceCivilPenaltyRequestTaskPayload taskPayload =
            (NonComplianceCivilPenaltyRequestTaskPayload) requestTask.getPayload();
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) requestTask.getRequest().getPayload();

        requestPayload.setCivilPenalty(taskPayload.getCivilPenalty());
        requestPayload.setCivilPenaltyAmount(taskPayload.getPenaltyAmount());
        requestPayload.setCivilPenaltyDueDate(taskPayload.getDueDate());
        requestPayload.setCivilPenaltyComments(taskPayload.getComments());
        requestPayload.setNonComplianceAttachments(taskPayload.getNonComplianceAttachments());
        requestPayload.setCivilPenaltyCompleted(taskPayload.getCivilPenaltyCompleted());

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
    }
}
