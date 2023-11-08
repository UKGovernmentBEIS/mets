package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.mapper.PermitSurrenderMapper;

@Service
@RequiredArgsConstructor
public class RequestPermitSurrenderService {
    
    private final RequestService requestService;
    private final PermitSurrenderSubmitValidatorService permitSurrenderSubmitValidatorService;
    private static final PermitSurrenderMapper permitSurrenderMapper = Mappers.getMapper(PermitSurrenderMapper.class);

    @Transactional
    public void applySavePayload(PermitSurrenderSaveApplicationRequestTaskActionPayload actionPayload,
            RequestTask requestTask) {
        PermitSurrenderApplicationSubmitRequestTaskPayload taskPayload = (PermitSurrenderApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        
        if(taskPayload == null) {
            taskPayload = PermitSurrenderApplicationSubmitRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD).build();
            requestTask.setPayload(taskPayload);
        }
        
        taskPayload.setPermitSurrender(actionPayload.getPermitSurrender());
        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
    }
    
    @Transactional
    public void applySubmitPayload(RequestTask requestTask, PmrvUser authUser) {
        Request request = requestTask.getRequest();
        PermitSurrenderApplicationSubmitRequestTaskPayload
            taskPayload = (PermitSurrenderApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        PermitSurrenderContainer permitSurrenderContainer = permitSurrenderMapper.toPermitSurrenderContainer(taskPayload);
        
        // validate permit surrender
        permitSurrenderSubmitValidatorService.validatePermitSurrender(permitSurrenderContainer);

        // update request payload
        PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        requestPayload.setPermitSurrender(taskPayload.getPermitSurrender());
        requestPayload.setPermitSurrenderAttachments(taskPayload.getPermitSurrenderAttachments());

        // add action
        PermitSurrenderApplicationSubmittedRequestActionPayload applicationSubmittedActionPayload = permitSurrenderMapper.toApplicationSubmittedRequestActionPayload(taskPayload);
        applicationSubmittedActionPayload.setPermitSurrenderAttachments(requestPayload.getPermitSurrenderAttachments());
        requestService.addActionToRequest(request, applicationSubmittedActionPayload, RequestActionType.PERMIT_SURRENDER_APPLICATION_SUBMITTED, authUser.getUserId());
    }
}
