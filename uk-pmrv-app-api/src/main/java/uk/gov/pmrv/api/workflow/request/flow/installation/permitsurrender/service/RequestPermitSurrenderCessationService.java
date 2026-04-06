package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountStatusService;
import uk.gov.pmrv.api.integration.registry.notification.installation.request.NotificationRegistryEvent;
import uk.gov.pmrv.api.integration.registry.notification.installation.request.RegistryNotificationType;
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
    private final ApplicationEventPublisher applicationEventPublisher;

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
    public void executeNotifyOperatorActions(RequestTask requestTask, AppUser appUser,
                                             NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {
        Request request = requestTask.getRequest();
        PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        PermitCessationSubmitRequestTaskPayload requestTaskPayload =
                (PermitCessationSubmitRequestTaskPayload) requestTask.getPayload();
        
        cessationNotifyOperatorValidator.validate(requestTask, appUser, taskActionPayload);

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
            requestPayload.getRegulatorAssignee());

        applicationEventPublisher.publishEvent(NotificationRegistryEvent.builder()
                .requestId(request.getId())
                .fileInfoDTO(cessationOfficialNotice)
                .accountId(request.getAccountId())
                .registryNotificationType(RegistryNotificationType.SURRENDER_CESSATION_NOTIFICATION).build());
        
        // send official notice
        permitSurrenderOfficialNoticeService.sendOfficialNoticeForDecisionNotification(request, cessationOfficialNotice, taskActionPayload.getDecisionNotification());
    }
}
