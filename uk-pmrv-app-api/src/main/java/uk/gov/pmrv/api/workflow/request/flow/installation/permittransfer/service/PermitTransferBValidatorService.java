package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetailsConfirmation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferViolation;

@Service
@Validated
@RequiredArgsConstructor
public class PermitTransferBValidatorService {

    private final InstallationAccountUpdateService installationAccountUpdateService;


    public void validateTransferDetailsConfirmation(@NotNull @Valid @SuppressWarnings("unused") final PermitTransferDetailsConfirmation detailsConfirmation) {
        // default validation
    }

    public void validateAndDisableTransferCodeStatus(final String transferCode) {

        try {
            installationAccountUpdateService.disableTransferCodeStatus(transferCode);
        } catch (final BusinessException e) {
            if (e.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND) {
                throw new BusinessException(
                    ErrorCode.FORM_VALIDATION,
                    PermitTransferViolation.INVALID_TRANSFER_CODE.getMessage()
                );
            } else {
                throw e;
            }
        }
    }
}
