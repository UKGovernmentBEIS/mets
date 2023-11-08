package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;

@Validated
@Service
@RequiredArgsConstructor
public class DoalProceedToAuthorityValidator {

    private final DoalSubmitValidator doalSubmitValidator;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void validateNotify(RequestTask requestTask, @NotNull @Valid DecisionNotification decisionNotification,
                               PmrvUser pmrvUser) {

        DoalApplicationSubmitRequestTaskPayload taskPayload =
                (DoalApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate task payload
        doalSubmitValidator.validate(taskPayload);

        // Validate determination
        validateDeterminationType(taskPayload.getDoal().getDetermination());

        DoalProceedToAuthorityDetermination determination =
                (DoalProceedToAuthorityDetermination) taskPayload.getDoal().getDetermination();
        if(!Boolean.TRUE.equals(determination.getNeedsOfficialNotice())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        // Validate
        final boolean valid = decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser);
        if (!valid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    public void validateComplete(RequestTask requestTask) {
        DoalApplicationSubmitRequestTaskPayload taskPayload =
                (DoalApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate task payload
        doalSubmitValidator.validate(taskPayload);

        // Validate determination
        validateDeterminationType(taskPayload.getDoal().getDetermination());

        DoalProceedToAuthorityDetermination determination =
                (DoalProceedToAuthorityDetermination) taskPayload.getDoal().getDetermination();
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
