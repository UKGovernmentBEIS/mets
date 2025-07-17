package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountStatusService;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.notification.PermitRevocationOfficialNoticeService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RequestPermitRevocationCessationService {
    
    private final PermitCessationNotifyOperatorValidator cessationNotifyOperatorValidator;
    private final InstallationAccountStatusService installationAccountStatusService;
    private final RequestService requestService;
    private final PermitRevocationOfficialNoticeService permitRevocationOfficialNoticeService;
    private final PermitCessationCompletedRequestActionPayloadMapper cessationCompletedRequestActionPayloadMapper;

    @Transactional
    public void applySaveCessation(final RequestTask requestTask,
                                   final PermitSaveCessationRequestTaskActionPayload taskActionPayload) {

        final PermitCessationSubmitRequestTaskPayload requestTaskPayload =
            (PermitCessationSubmitRequestTaskPayload) requestTask.getPayload();

        final PermitCessationContainer cessationContainer = requestTaskPayload.getCessationContainer();
        cessationContainer.setCessation(taskActionPayload.getCessation());

        requestTaskPayload.setCessationContainer(cessationContainer);
        requestTaskPayload.setCessationCompleted(taskActionPayload.getCessationCompleted());
    }

    @Transactional
    public void executeNotifyOperatorActions(final RequestTask requestTask,
                                             final AppUser appUser,
                                             final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {
    	final PermitCessationSubmitRequestTaskPayload requestTaskPayload =
                (PermitCessationSubmitRequestTaskPayload) requestTask.getPayload();
    	
        final Request request = requestTask.getRequest();
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();
        
        cessationNotifyOperatorValidator.validate(requestTask, appUser, taskActionPayload);

        installationAccountStatusService.handleRevocationCessationCompleted(request.getAccountId());
        
        //set cessation to request payload
        requestPayload.setPermitCessationContainer(requestTaskPayload.getCessationContainer());
        requestPayload.setPermitCessationCompletedDate(LocalDate.now());
        
        // generate official notice
        FileInfoDTO officialNotice = permitRevocationOfficialNoticeService.
            generateRevocationCessationOfficialNotice(request.getId(), taskActionPayload.getDecisionNotification());

        // add request action
        requestService.addActionToRequest(request,
            cessationCompletedRequestActionPayloadMapper.toCessationCompletedRequestActionPayload(requestTask,
                taskActionPayload,
                officialNotice,
                RequestActionPayloadType.PERMIT_REVOCATION_CESSATION_COMPLETED_PAYLOAD),
            RequestActionType.PERMIT_REVOCATION_CESSATION_COMPLETED,
            requestPayload.getRegulatorAssignee());
        
        // send official notice
        permitRevocationOfficialNoticeService.sendOfficialNotice(request, officialNotice, taskActionPayload.getDecisionNotification());
    }
}
