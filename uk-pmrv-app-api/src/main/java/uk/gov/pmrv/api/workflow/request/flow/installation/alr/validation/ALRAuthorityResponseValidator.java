package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAuthorityReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRGrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRPreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;


import java.util.Set;
import java.util.stream.Collectors;

@Validated
@Service
@RequiredArgsConstructor
public class ALRAuthorityResponseValidator {

    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final ALRTotalYearAllocationsValidator alrTotalYearAllocationsValidator;
    private final AllowanceAllocationValidator allowanceAllocationValidator;

    public void validate(RequestTask requestTask,
                         @NotNull @Valid ALRApplicationAuthorityReviewOutcome alrAuthorityReviewOutcome,
                         @NotNull @Valid DecisionNotification decisionNotification,
                         AppUser appUser) {

        // Validate authority response
        if(!alrAuthorityReviewOutcome.getAuthorityResponse().getType().equals(DoalAuthorityResponseType.INVALID)) {
            validateGrantResponse((ALRGrantAuthorityResponse) alrAuthorityReviewOutcome.getAuthorityResponse());
        }

        // Validate users
        final boolean valid = decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser);
        if (!valid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    private void validateGrantResponse(ALRGrantAuthorityResponse authorityResponse) {
        // Validate preliminary allocations
        Set<ALRPreliminaryAllocation> alrPreliminaryAllocations = authorityResponse.getPreliminaryAllocations();
        Set<PreliminaryAllocation> preliminaryAllocations = alrPreliminaryAllocations.stream()
                .map(pa -> (PreliminaryAllocation) pa)
                .collect(Collectors.toSet());
        if(!preliminaryAllocations.isEmpty() && !allowanceAllocationValidator.isValid(preliminaryAllocations)) {
            throw new BusinessException(MetsErrorCode.INVALID_ALR_PRELIMINARY_ALLOCATIONS,
                    preliminaryAllocations);
        }

        // Validate total allocations per year
        alrTotalYearAllocationsValidator.validate(alrPreliminaryAllocations, authorityResponse.getTotalAllocationsPerYear());
    }
}
