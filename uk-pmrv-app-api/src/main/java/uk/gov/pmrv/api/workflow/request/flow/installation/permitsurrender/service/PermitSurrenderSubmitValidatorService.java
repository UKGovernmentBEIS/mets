package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderViolation;

@Validated
@Service
@RequiredArgsConstructor
public class PermitSurrenderSubmitValidatorService {
    
    private final PermitSurrenderAttachmentsValidator permitSurrenderAttachmentsValidator;
    
    public void validatePermitSurrender(@Valid PermitSurrenderContainer permitSurrenderContainer) {
        if (!permitSurrenderAttachmentsValidator.attachmentsExist(permitSurrenderContainer.getPermitSurrender().getAttachmentIds())) {
            throw new BusinessException(MetsErrorCode.INVALID_PERMIT_SURRENDER,
                    PermitSurrenderViolation.ATTACHMENT_NOT_FOUND.getMessage());
        }
        
        if (!permitSurrenderAttachmentsValidator.sectionAttachmentsReferencedInPermitSurrender(
                permitSurrenderContainer.getPermitSurrender().getAttachmentIds(),
                permitSurrenderContainer.getPermitSurrenderAttachments().keySet())) {
            throw new BusinessException(MetsErrorCode.INVALID_PERMIT_SURRENDER,
                    PermitSurrenderViolation.ATTACHMENT_NOT_REFERENCED.getMessage());
        }
    }
}
