package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthority;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalGrantAuthorityWithCorrectionsResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRejectAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.time.Year;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalAuthorityResponseValidatorTest {

    @InjectMocks
    private DoalAuthorityResponseValidator validator;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Mock
    private DoalTotalYearAllocationsValidator doalTotalYearAllocationsValidator;

    @Mock
    private AllowanceAllocationValidator allowanceAllocationValidator;

    @Test
    void validate() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final Set<PreliminaryAllocation> preliminaryAllocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2022))
                        .allowances(10)
                        .build()
        );
        final Map<Year, Integer> totalAllocationsPerYear = Map.of(Year.of(2022), 10);

        final DoalAuthority doalAuthority = DoalAuthority.builder()
                .authorityResponse(DoalGrantAuthorityWithCorrectionsResponse.builder()
                        .type(DoalAuthorityResponseType.VALID_WITH_CORRECTIONS)
                        .preliminaryAllocations(new TreeSet<>(preliminaryAllocations))
                        .totalAllocationsPerYear(new TreeMap<>(totalAllocationsPerYear))
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();

        when(allowanceAllocationValidator.isValid(preliminaryAllocations)).thenReturn(true);
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser))
                .thenReturn(true);

        // Invoke
        validator.validate(requestTask, doalAuthority, decisionNotification, appUser);

        // Verify
        verify(allowanceAllocationValidator, times(1)).isValid(preliminaryAllocations);
        verify(doalTotalYearAllocationsValidator, times(1)).validate(preliminaryAllocations, totalAllocationsPerYear);
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, appUser);
    }

    @Test
    void validate_not_valid_allocations() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final Set<PreliminaryAllocation> preliminaryAllocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2022))
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2023))
                        .allowances(20)
                        .build()
        );
        final Map<Year, Integer> totalAllocationsPerYear = Map.of(Year.of(2022), 20);

        final DoalAuthority doalAuthority = DoalAuthority.builder()
                .authorityResponse(DoalGrantAuthorityWithCorrectionsResponse.builder()
                        .type(DoalAuthorityResponseType.VALID_WITH_CORRECTIONS)
                        .preliminaryAllocations(new TreeSet<>(preliminaryAllocations))
                        .totalAllocationsPerYear(new TreeMap<>(totalAllocationsPerYear))
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();

        when(allowanceAllocationValidator.isValid(preliminaryAllocations)).thenReturn(false);

        // Invoke
        final BusinessException businessException = assertThrows(BusinessException.class,
                () -> validator.validate(requestTask, doalAuthority, decisionNotification, appUser));

        // Verify
        assertEquals(MetsErrorCode.INVALID_DOAL, businessException.getErrorCode());
        verify(allowanceAllocationValidator, times(1)).isValid(preliminaryAllocations);
        verifyNoInteractions(doalTotalYearAllocationsValidator, decisionNotificationUsersValidator);
    }

    @Test
    void validate_not_valid_users() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final DoalAuthority doalAuthority = DoalAuthority.builder()
                .authorityResponse(DoalRejectAuthorityResponse.builder()
                        .type(DoalAuthorityResponseType.INVALID)
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser))
                .thenReturn(false);

        // Invoke
        final BusinessException businessException = assertThrows(BusinessException.class,
                () -> validator.validate(requestTask, doalAuthority, decisionNotification, appUser));

        // Verify
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, appUser);
        verifyNoInteractions(allowanceAllocationValidator, doalTotalYearAllocationsValidator);
    }
}
