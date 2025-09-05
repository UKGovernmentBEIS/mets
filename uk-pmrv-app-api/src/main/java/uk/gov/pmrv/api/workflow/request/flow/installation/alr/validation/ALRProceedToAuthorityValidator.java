package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.DoalDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;

@Validated
@Service
@RequiredArgsConstructor
public class ALRProceedToAuthorityValidator {

    private final ALRValidationService alrValidationService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void validateNotify(RequestTask requestTask, @NotNull @Valid DecisionNotification decisionNotification,
                               AppUser appUser) {

        ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate task payload
        alrValidationService.validateRegulatorSubmitTaskPayload(taskPayload);

        // Validate determination
        validateDeterminationType(taskPayload.getRegulatorReviewOutcome().getDetermination());

        DoalProceedToAuthorityDetermination determination =
                (DoalProceedToAuthorityDetermination) taskPayload.getRegulatorReviewOutcome().getDetermination();
        if(!Boolean.TRUE.equals(determination.getNeedsOfficialNotice())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        // Validate
        final boolean valid = decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser);
        if (!valid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    public void validateComplete(RequestTask requestTask) {
        ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate task payload
        alrValidationService.validateRegulatorSubmitTaskPayload(taskPayload);

        // Validate determination
        validateDeterminationType(taskPayload.getRegulatorReviewOutcome().getDetermination());

        DoalProceedToAuthorityDetermination determination =
                (DoalProceedToAuthorityDetermination) taskPayload.getRegulatorReviewOutcome().getDetermination();
        if(!Boolean.FALSE.equals(determination.getNeedsOfficialNotice())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    private void validateDeterminationType(DoalDetermination determination) {
        if(!determination.getType().equals(DoalDeterminationType.PROCEED_TO_AUTHORITY)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}
