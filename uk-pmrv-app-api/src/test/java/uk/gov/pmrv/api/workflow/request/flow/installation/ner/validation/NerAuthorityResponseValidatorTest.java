package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.GrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;

@ExtendWith(MockitoExtension.class)
class NerAuthorityResponseValidatorTest {

    @InjectMocks
    private NerAuthorityResponseValidator validator;

    @Mock
    private AllowanceAllocationValidator allowanceAllocationValidator;

    @Test
    void validate_whenDuplicatesExist_thenException() {
        final Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder().year(Year.of(2022))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .allowances(10).build(),
                PreliminaryAllocation.builder().year(Year.of(2023))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .allowances(20).build()
        );
        final NerAuthorityResponseRequestTaskPayload payload = NerAuthorityResponseRequestTaskPayload.builder()
            .authorityResponse(GrantAuthorityResponse.builder()
                .preliminaryAllocations(new TreeSet<>(allocations)).build()).build();

        when(allowanceAllocationValidator.isValid(allocations)).thenReturn(false);

        final BusinessException businessException = assertThrows(
            BusinessException.class, () -> validator.validateAuthorityResponse(payload)
        );

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
        verify(allowanceAllocationValidator, times(1)).isValid(allocations);
    }

    @Test
    void validate_whenNoDuplicatesExist_thenOK() {
        final Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder().year(Year.of(2021))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .allowances(10).build(),
                PreliminaryAllocation.builder().year(Year.of(2022))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .allowances(20).build()
        );
        final NerAuthorityResponseRequestTaskPayload payload = NerAuthorityResponseRequestTaskPayload.builder()
            .authorityResponse(GrantAuthorityResponse.builder()
                .preliminaryAllocations(new TreeSet<>(allocations)).build()).build();

        when(allowanceAllocationValidator.isValid(allocations)).thenReturn(true);
        
        assertDoesNotThrow(() -> validator.validateAuthorityResponse(payload));

        verify(allowanceAllocationValidator, times(1)).isValid(allocations);
    }
}
