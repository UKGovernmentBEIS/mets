package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthority;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalGrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalViolation;

import java.util.Set;

@Validated
@Service
@RequiredArgsConstructor
public class DoalAuthorityResponseValidator {

    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final DoalTotalYearAllocationsValidator doalTotalYearAllocationsValidator;
    private final AllowanceAllocationValidator allowanceAllocationValidator;

    public void validate(RequestTask requestTask,
                         @NotNull @Valid DoalAuthority doalAuthority,
                         @NotNull @Valid DecisionNotification decisionNotification,
                         PmrvUser pmrvUser) {

        // Validate authority response
        if(!doalAuthority.getAuthorityResponse().getType().equals(DoalAuthorityResponseType.INVALID)) {
            validateGrantResponse((DoalGrantAuthorityResponse) doalAuthority.getAuthorityResponse());
        }

        // Validate users
        final boolean valid = decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser);
        if (!valid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    private void validateGrantResponse(DoalGrantAuthorityResponse authorityResponse) {
        // Validate preliminary allocations
        Set<PreliminaryAllocation> preliminaryAllocations = authorityResponse.getPreliminaryAllocations();
        if(!preliminaryAllocations.isEmpty() && !allowanceAllocationValidator.isValid(preliminaryAllocations)) {
            throw new BusinessException(ErrorCode.INVALID_DOAL,
                    DoalViolation.INVALID_PRELIMINARY_ALLOCATIONS.getMessage());
        }

        // Validate total allocations per year
        doalTotalYearAllocationsValidator.validate(preliminaryAllocations, authorityResponse.getTotalAllocationsPerYear());
    }
}
