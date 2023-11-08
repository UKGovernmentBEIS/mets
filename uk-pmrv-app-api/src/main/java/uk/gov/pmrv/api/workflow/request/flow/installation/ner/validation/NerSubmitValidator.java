package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerViolation;

@Service
@Validated
@RequiredArgsConstructor
public class NerSubmitValidator {

    private final NerAttachmentsValidator attachmentsValidator;

    public void validateSubmitTaskPayload(@NotNull @Valid final NerApplicationSubmitRequestTaskPayload taskPayload) {

        if (!attachmentsValidator.attachmentsExist(taskPayload.getReferencedAttachmentIds())) {
            throw new BusinessException(ErrorCode.INVALID_NER,
                NerViolation.ATTACHMENT_NOT_FOUND.getMessage());
        }

        if (!attachmentsValidator.attachmentsReferenced(
            taskPayload.getReferencedAttachmentIds(),
            taskPayload.getAttachments().keySet())
        ) {
            throw new BusinessException(ErrorCode.INVALID_NER,
                NerViolation.ATTACHMENT_NOT_REFERENCED.getMessage());
        }
    }
}
