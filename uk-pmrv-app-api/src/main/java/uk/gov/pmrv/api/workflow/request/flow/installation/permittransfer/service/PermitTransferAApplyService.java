package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferASaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferOperatorInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper.PermitTransferMapper;


@Service
@RequiredArgsConstructor
public class PermitTransferAApplyService {

    private static final PermitTransferMapper PERMIT_TRANSFER_MAPPER = Mappers.getMapper(PermitTransferMapper.class);

    private final PermitTransferAValidatorService validatorService;
    private final RequestService requestService;
    private final InstallationAccountQueryService installationAccountQueryService;

    @Transactional
    public void applySaveAction(final RequestTask requestTask,
                                final PermitTransferASaveApplicationRequestTaskActionPayload actionPayload) {

        final PermitTransferAApplicationRequestTaskPayload taskPayload =
            (PermitTransferAApplicationRequestTaskPayload) requestTask.getPayload();

        final PermitTransferDetails permitTransferDetails =
                actionPayload.getPermitTransferDetails();
        taskPayload.setPermitTransferDetails(actionPayload.getPermitTransferDetails());
        taskPayload.setSectionCompleted(actionPayload.getSectionCompleted());

        if (permitTransferDetails == null) {
            return;
        }

        // Always derive operator information server-side.
        permitTransferDetails.setReceiver(null);
        permitTransferDetails.setTransferrer(null);

        final String transferCode = permitTransferDetails.getTransferCode();
        if (transferCode == null) {
            return;
        }

        installationAccountQueryService.getByActiveTransferCode(transferCode)
                .map(this::toPermitTransferOperatorInfo)
                .ifPresent(permitTransferDetails::setReceiver);

        final Long transferringAccountId = requestTask.getRequest().getAccountId();
        final InstallationAccountDTO transferringAccount =
                installationAccountQueryService.getAccountDTOById(transferringAccountId);

        if (transferringAccount != null) {
            permitTransferDetails.setTransferrer(
                    toPermitTransferOperatorInfo(transferringAccount));
        }
    }
    
    public void applySubmitAction(final RequestTask requestTask, final AppUser appUser) {

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
            appUser.getUserId()
        );
    }

    private PermitTransferOperatorInfo toPermitTransferOperatorInfo(
            final InstallationAccountDTO account) {

        return PermitTransferOperatorInfo.builder()
                .id(account.getEmitterId())
                .name(account.getName())
                .build();
    }
}
