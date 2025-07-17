package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationViolation;

@Validated
@Service
@RequiredArgsConstructor
public class PermitNotificationSubmitValidatorService {

    private final PermitNotificationAttachmentsValidator permitNotificationAttachmentsValidator;

    public void validatePermitNotification(@Valid PermitNotificationContainer permitNotificationContainer) {
        if (!permitNotificationAttachmentsValidator.attachmentsExist(permitNotificationContainer.getPermitNotification().getAttachmentIds())) {
            throw new BusinessException(MetsErrorCode.INVALID_PERMIT_NOTIFICATION,
                    PermitNotificationViolation.ATTACHMENT_NOT_FOUND.getMessage());
        }

        if (!permitNotificationAttachmentsValidator.sectionAttachmentsReferencedInPermitNotification(
                permitNotificationContainer.getPermitNotification().getAttachmentIds(),
                permitNotificationContainer.getPermitNotificationAttachments().keySet())) {
            throw new BusinessException(MetsErrorCode.INVALID_PERMIT_NOTIFICATION,
                    PermitNotificationViolation.ATTACHMENT_NOT_REFERENCED.getMessage());
        }
    }
}
