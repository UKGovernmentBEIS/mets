package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalViolation;

import java.util.Set;

@Validated
@Service
@RequiredArgsConstructor
public class DoalSubmitValidator {

    private final DoalAttachmentsValidator doalAttachmentsValidator;
    private final AllowanceAllocationValidator allowanceAllocationValidator;

    public void validate(@NotNull @Valid DoalApplicationSubmitRequestTaskPayload taskPayload) {
        final Doal doal = taskPayload.getDoal();

        // Validate Doal Data
        validateDoal(doal);

        // Validate Proceed to Authority
        if(doal.getDetermination().getType().equals(DoalDeterminationType.PROCEED_TO_AUTHORITY)) {
            validateProceedToAuthorityDetermination(doal);
        }

        // Validate attachments
        if (!doalAttachmentsValidator.attachmentsExist(taskPayload.getReferencedAttachmentIds())) {
            throw new BusinessException(ErrorCode.INVALID_DOAL,
                    DoalViolation.ATTACHMENT_NOT_FOUND.getMessage());
        }

        if (!doalAttachmentsValidator.attachmentsReferenced(
                taskPayload.getReferencedAttachmentIds(),
                taskPayload.getAttachments().keySet())
        ) {
            throw new BusinessException(ErrorCode.INVALID_DOAL,
                    DoalViolation.ATTACHMENT_NOT_REFERENCED.getMessage());
        }
    }

    private void validateDoal(final Doal doal) {
        Set<PreliminaryAllocation> preliminaryAllocations = doal.getActivityLevelChangeInformation().getPreliminaryAllocations();

        if(!preliminaryAllocations.isEmpty() && !allowanceAllocationValidator.isValid(preliminaryAllocations)) {
                throw new BusinessException(ErrorCode.INVALID_DOAL,
                        DoalViolation.INVALID_PRELIMINARY_ALLOCATIONS.getMessage());
        }
    }

    private void validateProceedToAuthorityDetermination(final Doal doal) {
        // Validate article reasons
        DoalProceedToAuthorityDetermination determination =
                (DoalProceedToAuthorityDetermination) doal.getDetermination();
        boolean areArticleReasonsValid = determination.getArticleReasonItems().stream()
                .allMatch(item -> item.getGroupType().equals(determination.getArticleReasonGroupType()));

        if(!areArticleReasonsValid) {
            throw new BusinessException(ErrorCode.INVALID_DOAL,
                    DoalViolation.INVALID_ARTICLE_REASONS.getMessage());
        }
    }
}
