package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import java.util.Optional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferViolation;

@Service
@Validated
@RequiredArgsConstructor
public class PermitTransferAValidatorService {
    
    private final PermitTransferAttachmentsValidator attachmentsValidator;
    private final RequestRepository requestRepository;
    private final InstallationAccountQueryService installationAccountQueryService;


    public void validateTaskPayload(@NotNull @Valid final PermitTransferAApplicationRequestTaskPayload taskPayload) {

        if (!attachmentsValidator.attachmentsExist(taskPayload.getReferencedAttachmentIds())) {
            throw new BusinessException(ErrorCode.INVALID_PERMIT_TRANSFER,
                PermitTransferViolation.ATTACHMENT_NOT_FOUND.getMessage());
        }

        if (!attachmentsValidator.sectionAttachmentsReferenced(
            taskPayload.getReferencedAttachmentIds(),
            taskPayload.getTransferAttachments().keySet())
        ) {
            throw new BusinessException(ErrorCode.INVALID_PERMIT_TRANSFER,
                PermitTransferViolation.ATTACHMENT_NOT_REFERENCED.getMessage());
        }
    }
    
    public void validatePermitTransferA(final RequestTask requestTask) {

        final PermitTransferAApplicationRequestTaskPayload taskPayload =
            (PermitTransferAApplicationRequestTaskPayload) requestTask.getPayload();
        final String transferCode = taskPayload.getPermitTransferDetails().getTransferCode();
    
        // validate transfer code exists and is active
        final Optional<InstallationAccountDTO> receivingAccount = installationAccountQueryService.getByActiveTransferCode(transferCode);
        if (receivingAccount.isEmpty()) {
            throw new BusinessException(
                ErrorCode.FORM_VALIDATION,
                PermitTransferViolation.INVALID_TRANSFER_CODE.getMessage()
            );
        }
        
        // validate that there is not another TRANSFER_B request for receiving account
        final boolean openTransferBExists =
            !requestRepository.findByAccountIdAndTypeAndStatus(
                    receivingAccount.get().getId(),
                    RequestType.PERMIT_TRANSFER_B,
                    RequestStatus.IN_PROGRESS)
                .isEmpty();

        if (openTransferBExists) {
            throw new BusinessException(
                ErrorCode.FORM_VALIDATION,
                PermitTransferViolation.INVALID_TRANSFER_CODE.getMessage()
            );
        }
    }
}
