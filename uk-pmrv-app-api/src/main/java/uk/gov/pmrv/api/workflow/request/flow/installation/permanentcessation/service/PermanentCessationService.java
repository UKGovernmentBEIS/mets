package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesSaveApplicationRequestTaskActionPayload;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PermanentCessationService {
    private final RequestService requestService;

    public void cancel(String requestId) {
        final Request request = requestService.findRequestById(requestId);

        requestService.addActionToRequest(request, null,
                RequestActionType.PERMANENT_CESSATION_APPLICATION_CANCELLED,
                request.getPayload().getRegulatorAssignee());
    }

    @Transactional
    public void savePermanentCessationDecisionNotification(
            final NotifyOperatorForDecisionRequestTaskActionPayload actionPayload,
            final RequestTask requestTask) {

        final Request request = requestTask.getRequest();
        final PermanentCessationApplicationSubmitRequestTaskPayload taskPayload =
                (PermanentCessationApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        final PermanentCessationRequestPayload requestPayload =
                (PermanentCessationRequestPayload) request.getPayload();

        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);

        requestPayload.setDecisionNotification(actionPayload.getDecisionNotification());
        requestPayload.setPermanentCessation(taskPayload.getPermanentCessation());
        requestPayload.setPermanentCessationAttachments(taskPayload.getPermanentCessationAttachments());
        requestPayload.setPermanentCessationSectionsCompleted(taskPayload.getPermanentCessationSectionsCompleted());
    }


    @Transactional
    public void applySavePayload(final PermanentCessationSaveApplicationRequestTaskActionPayload actionPayload,
                                 final RequestTask requestTask) {

        final PermanentCessationApplicationSubmitRequestTaskPayload taskPayload =
                (PermanentCessationApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        taskPayload.setPermanentCessation(actionPayload.getPermanentCessation());
        taskPayload.setPermanentCessationSectionsCompleted(actionPayload.getPermanentCessationSectionsCompleted());
        taskPayload.setPermanentCessationAttachments(actionPayload.getPermanentCessationAttachments());
    }

    public void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser) {
        final PermanentCessationRequestPayload requestPayload =
                (PermanentCessationRequestPayload) requestTask.getRequest().getPayload();
        final PermanentCessationApplicationSubmitRequestTaskPayload requestTaskPayload =
                (PermanentCessationApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        requestPayload.setRegulatorReviewer(appUser.getUserId());

        final PermanentCessation permanentCessation = requestTaskPayload.getPermanentCessation();
        requestPayload.setPermanentCessation(permanentCessation);
        requestPayload
                .setPermanentCessationSectionsCompleted(requestTaskPayload
                        .getPermanentCessationSectionsCompleted());
        requestPayload.setPermanentCessationAttachments(requestTaskPayload.getPermanentCessationAttachments());
    }
}
