package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@Service
@Validated
@RequiredArgsConstructor
public class NonComplianceApplicationValidator {
    
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    
    public void validateApplication(@NotNull @Valid final NonComplianceApplicationSubmitRequestTaskPayload taskPayload) {
        
        // check that the ids of the selected requests are valid
        final List<RequestInfoDTO> availableRequests = taskPayload.getAvailableRequests();
        final Set<String> selectedRequests = taskPayload.getSelectedRequests();
        final Set<String> availableRequestIds = availableRequests.stream().map(RequestInfoDTO::getId).collect(Collectors.toSet());
        final boolean requestsValid = availableRequestIds.containsAll(selectedRequests);
        if (!requestsValid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    public void validateDailyPenalty(@NotNull @Valid @SuppressWarnings("unused")
                                     final NonComplianceDailyPenaltyNoticeRequestTaskPayload taskPayload) {
        // default validation   
    }

    public void validateNoticeOfIntent(@NotNull @Valid @SuppressWarnings("unused")
                                       final NonComplianceNoticeOfIntentRequestTaskPayload taskPayload) {
        // default validation   
    }

    public void validateCivilPenalty(@NotNull @Valid @SuppressWarnings("unused")
                                       final NonComplianceCivilPenaltyRequestTaskPayload taskPayload) {
        // default validation   
    }

    public void validateUsers(final RequestTask requestTask,
                              final Set<String> operators,
                              final Set<Long> externalContacts,
                              final AppUser appUser) {
        
        final DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(operators)
            .externalContacts(externalContacts)
            .build();
        
        final boolean valid = decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser);
        if (!valid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    public void validateFinalDetermination(@NotNull @Valid @SuppressWarnings("unused")
                                           final NonComplianceFinalDeterminationRequestTaskPayload taskPayload) {
        // default validation
    }
    
    public void validateContactAddress(final Request request) {
    	final boolean valid = decisionNotificationUsersValidator.shouldHaveContactAddress(request);
        if (!valid) {
            throw new BusinessException(MetsErrorCode.AVIATION_ACCOUNT_LOCATION_NOT_EXIST);
        }
    }
}
