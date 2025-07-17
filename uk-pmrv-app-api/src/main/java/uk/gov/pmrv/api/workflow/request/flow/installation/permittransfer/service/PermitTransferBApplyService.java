package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetailsConfirmation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper.PermitTransferMapper;

@Service
@RequiredArgsConstructor
public class PermitTransferBApplyService {

    private static final PermitTransferMapper PERMIT_TRANSFER_MAPPER = Mappers.getMapper(PermitTransferMapper.class);

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final PermitTransferBValidatorService transferValidatorService;
    private final PermitValidatorService permitValidatorService;
    private final RequestService requestService;
    
    
    @Transactional
    public void applySaveAction(final RequestTask requestTask,
                                final PermitTransferBSaveApplicationRequestTaskActionPayload actionPayload) {

        final PermitTransferBApplicationRequestTaskPayload taskPayload =
            (PermitTransferBApplicationRequestTaskPayload) requestTask.getPayload();
        
        taskPayload.setPermitTransferDetailsConfirmation(actionPayload.getPermitTransferDetailsConfirmation());
        taskPayload.setPermit(actionPayload.getPermit());
        taskPayload.setPermitType(actionPayload.getPermitType());
        taskPayload.setPermitSectionsCompleted(actionPayload.getPermitSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(final RequestTask requestTask, final AppUser appUser) {

        final Request request = requestTask.getRequest();
        final PermitTransferBApplicationRequestTaskPayload taskPayload =
            (PermitTransferBApplicationRequestTaskPayload) requestTask.getPayload();
        final PermitTransferDetailsConfirmation transferDetailsConfirmation =
            taskPayload.getPermitTransferDetailsConfirmation();
        final String transferCode = taskPayload.getPermitTransferDetails().getTransferCode();
        
        final InstallationOperatorDetails installationOperatorDetails =
            installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId());
        final PermitContainer permitContainer =
            PERMIT_TRANSFER_MAPPER.toPermitContainer(taskPayload, installationOperatorDetails);
        
        // validate
        permitValidatorService.validatePermit(permitContainer);
        transferValidatorService.validateTransferDetailsConfirmation(transferDetailsConfirmation);
        transferValidatorService.validateAndDisableTransferCodeStatus(transferCode);

        // update request payload
        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();
        requestPayload.setPermitTransferDetailsConfirmation(transferDetailsConfirmation);
        requestPayload.setPermit(taskPayload.getPermit());
        requestPayload.setPermitType(taskPayload.getPermitType());
        requestPayload.setPermitAttachments(taskPayload.getPermitAttachments());
        requestPayload.setPermitSectionsCompleted(taskPayload.getPermitSectionsCompleted());
        
        // add action
        final PermitTransferBApplicationSubmittedRequestActionPayload actionPayload =
            PERMIT_TRANSFER_MAPPER.toPermitTransferBApplicationSubmitted(taskPayload, installationOperatorDetails);
        actionPayload.setPermitAttachments(taskPayload.getPermitAttachments());
        requestService.addActionToRequest(
            request,
            actionPayload,
            RequestActionType.PERMIT_TRANSFER_B_APPLICATION_SUBMITTED,
            appUser.getUserId()
        );
    }
}
