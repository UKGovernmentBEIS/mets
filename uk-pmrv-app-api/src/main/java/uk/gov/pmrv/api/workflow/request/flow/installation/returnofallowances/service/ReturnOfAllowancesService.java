package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesSaveApplicationRequestTaskActionPayload;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReturnOfAllowancesService {

    @Transactional
    public void applySavePayload(final ReturnOfAllowancesSaveApplicationRequestTaskActionPayload actionPayload,
                                 final RequestTask requestTask) {

        final ReturnOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            (ReturnOfAllowancesApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
        taskPayload.setReturnOfAllowances(actionPayload.getReturnOfAllowances());
    }

    @Transactional
    public void saveReturnOfAllowancesDecisionNotification(
        final NotifyOperatorForDecisionRequestTaskActionPayload actionPayload,
        final RequestTask requestTask) {

        final Request request = requestTask.getRequest();
        final ReturnOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            (ReturnOfAllowancesApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        final ReturnOfAllowancesRequestPayload requestPayload =
            (ReturnOfAllowancesRequestPayload) request.getPayload();

        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);

        requestPayload.setDecisionNotification(actionPayload.getDecisionNotification());
        requestPayload.setReturnOfAllowancesSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setReturnOfAllowances(taskPayload.getReturnOfAllowances());
    }

    @Transactional
    public void requestPeerReview(final RequestTask requestTask,
                                  final String selectedPeerReviewer,
                                  final String regulatorReviewer) {

        final ReturnOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            (ReturnOfAllowancesApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final ReturnOfAllowancesRequestPayload requestPayload = (ReturnOfAllowancesRequestPayload) request.getPayload();

        requestPayload.setReturnOfAllowances(taskPayload.getReturnOfAllowances());
        requestPayload.setReturnOfAllowancesSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setRegulatorReviewer(regulatorReviewer);
        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
    }
}
