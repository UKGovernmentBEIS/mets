package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalSaveApplicationRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesWithdrawalService {

    @Transactional
    public void applySavePayload(final WithholdingOfAllowancesWithdrawalSaveApplicationRequestTaskActionPayload actionPayload,
                                 final RequestTask requestTask) {

        final WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload taskPayload =
            (WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
        taskPayload.setWithholdingWithdrawal(actionPayload.getWithholdingWithdrawal());
    }

    public void saveWithholdingOfAllowancesWithdrawalDecisionNotification(
        NotifyOperatorForDecisionRequestTaskActionPayload actionPayload,
        RequestTask requestTask) {
        final Request request = requestTask.getRequest();
        final WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload taskPayload =
            (WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        final WithholdingOfAllowancesRequestPayload requestPayload =
            (WithholdingOfAllowancesRequestPayload) request.getPayload();

        requestPayload.setWithdrawDecisionNotification(actionPayload.getDecisionNotification());
        requestPayload.setWithholdingOfAllowancesWithdrawalSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setWithholdingWithdrawal(taskPayload.getWithholdingWithdrawal());
    }

    public void applyCloseAction(RequestTask requestTask,
                                 WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload taskActionPayload) {

        WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload requestTaskPayload =
            (WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setCloseJustification(taskActionPayload.getCloseJustification());

        final WithholdingOfAllowancesRequestPayload requestPayload =
            (WithholdingOfAllowancesRequestPayload) requestTask.getRequest().getPayload();

        requestPayload.setCloseJustification(taskActionPayload.getCloseJustification());
        requestPayload.setWithholdingOfAllowancesWithdrawalAttachments(requestTaskPayload.getWithholdingWithdrawalAttachments());
    }
}
