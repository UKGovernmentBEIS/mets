package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferASaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper.PermitTransferMapper;

@Service
@RequiredArgsConstructor
public class PermitTransferAApplyService {

    private static final PermitTransferMapper PERMIT_TRANSFER_MAPPER = Mappers.getMapper(PermitTransferMapper.class);

    private final PermitTransferAValidatorService validatorService;
    private final RequestService requestService;

    @Transactional
    public void applySaveAction(final RequestTask requestTask,
                                final PermitTransferASaveApplicationRequestTaskActionPayload actionPayload) {

        final PermitTransferAApplicationRequestTaskPayload taskPayload =
            (PermitTransferAApplicationRequestTaskPayload) requestTask.getPayload();

        taskPayload.setPermitTransferDetails(actionPayload.getPermitTransferDetails());
        taskPayload.setSectionCompleted(actionPayload.getSectionCompleted());
    }
    
    public void applySubmitAction(final RequestTask requestTask, final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final PermitTransferAApplicationRequestTaskPayload taskPayload =
            (PermitTransferAApplicationRequestTaskPayload) requestTask.getPayload();
        
        // validate
        validatorService.validateTaskPayload(taskPayload);
        validatorService.validatePermitTransferA(requestTask);

        // update request payload
        final PermitTransferARequestPayload requestPayload = (PermitTransferARequestPayload) request.getPayload();
        requestPayload.setPermitTransferDetails(taskPayload.getPermitTransferDetails());
        requestPayload.setTransferAttachments(taskPayload.getTransferAttachments());
        
        // add action
        final PermitTransferAApplicationSubmittedRequestActionPayload actionPayload =
            PERMIT_TRANSFER_MAPPER.toPermitTransferAApplicationSubmitted(taskPayload);
        actionPayload.setTransferAttachments(taskPayload.getTransferAttachments());
        requestService.addActionToRequest(
            request,
            actionPayload,
            RequestActionType.PERMIT_TRANSFER_A_APPLICATION_SUBMITTED,
            pmrvUser.getUserId()
        );
    }
}
