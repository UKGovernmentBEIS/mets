package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesService {

    @Transactional
    public void applySavePayload(final WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload actionPayload,
                                 final RequestTask requestTask) {

        final WithholdingOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            (WithholdingOfAllowancesApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
        taskPayload.setWithholdingOfAllowances(actionPayload.getWithholdingOfAllowances());
    }

    @Transactional
    public void saveWithholdingOfAllowancesDecisionNotification(
        final NotifyOperatorForDecisionRequestTaskActionPayload actionPayload,
        final RequestTask requestTask) {

        final Request request = requestTask.getRequest();
        final WithholdingOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            (WithholdingOfAllowancesApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        final WithholdingOfAllowancesRequestPayload requestPayload =
            (WithholdingOfAllowancesRequestPayload) request.getPayload();

        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);

        requestPayload.setDecisionNotification(actionPayload.getDecisionNotification());
        requestPayload.setWithholdingOfAllowancesSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setWithholdingOfAllowances(taskPayload.getWithholdingOfAllowances());
    }

    @Transactional
    public void requestPeerReview(final RequestTask requestTask,
                                  final String selectedPeerReviewer,
                                  final String regulatorReviewer) {

        final WithholdingOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            (WithholdingOfAllowancesApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final WithholdingOfAllowancesRequestPayload requestPayload = (WithholdingOfAllowancesRequestPayload) request.getPayload();

        requestPayload.setWithholdingOfAllowances(taskPayload.getWithholdingOfAllowances());
        requestPayload.setWithholdingOfAllowancesSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setRegulatorReviewer(regulatorReviewer);
        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
    }
}
