package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountStatusService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitSaveCessationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.mapper.PermitCessationCompletedRequestActionPayloadMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitCessationNotifyOperatorValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.notification.PermitSurrenderOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class RequestPermitSurrenderCessationService {

    private final RequestService requestService;
    private final PermitCessationNotifyOperatorValidator cessationNotifyOperatorValidator;
    private final PermitCessationCompletedRequestActionPayloadMapper cessationCompletedRequestActionPayloadMapper;
    private final InstallationAccountStatusService installationAccountStatusService;
    private final PermitSurrenderOfficialNoticeService permitSurrenderOfficialNoticeService;

    @Transactional
    public void applySaveCessation(RequestTask requestTask, PermitSaveCessationRequestTaskActionPayload taskActionPayload) {
        PermitCessationSubmitRequestTaskPayload requestTaskPayload =
            (PermitCessationSubmitRequestTaskPayload)requestTask.getPayload();

        PermitCessationContainer cessationContainer = requestTaskPayload.getCessationContainer();
        cessationContainer.setCessation(taskActionPayload.getCessation());

        requestTaskPayload.setCessationContainer(cessationContainer);
        requestTaskPayload.setCessationCompleted(taskActionPayload.getCessationCompleted());
    }

    @Transactional
    public void executeNotifyOperatorActions(RequestTask requestTask, PmrvUser pmrvUser,
                                             NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {
        Request request = requestTask.getRequest();
        PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        PermitCessationSubmitRequestTaskPayload requestTaskPayload =
                (PermitCessationSubmitRequestTaskPayload) requestTask.getPayload();
        
        cessationNotifyOperatorValidator.validate(requestTask, pmrvUser, taskActionPayload);

        // update account status
        installationAccountStatusService.handleSurrenderCessationCompleted(request.getAccountId());

        // save cessation to request payload
        requestPayload.setPermitCessation(requestTaskPayload.getCessationContainer().getCessation());
        
        // generate official notice
        FileInfoDTO cessationOfficialNotice = permitSurrenderOfficialNoticeService
            .generateCessationOfficialNotice(request, taskActionPayload.getDecisionNotification());
        
        // add request action
        requestService.addActionToRequest(request,
            cessationCompletedRequestActionPayloadMapper.toCessationCompletedRequestActionPayload(requestTask, 
                taskActionPayload,
                cessationOfficialNotice,
                RequestActionPayloadType.PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD),
            RequestActionType.PERMIT_SURRENDER_CESSATION_COMPLETED,
            requestPayload.getRegulatorReviewer());
        
        // send official notice
        permitSurrenderOfficialNoticeService.sendOfficialNotice(request, cessationOfficialNotice, taskActionPayload.getDecisionNotification());
    }
}
