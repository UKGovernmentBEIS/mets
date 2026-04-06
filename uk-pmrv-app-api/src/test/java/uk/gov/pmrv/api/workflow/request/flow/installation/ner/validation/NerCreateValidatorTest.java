package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class NerCreateValidatorTest {

    @InjectMocks
    private NerCreateValidator validator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private RequestRepository requestRepository;

    @Test
    void validate_whenEmitterTypeIsNotGHGE_thenUnavailable() {
        final long accountId = 1L;

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder()
                        .emitterType(EmitterType.HSE)
                        .faStatus(false)
                        .build());

        when(requestRepository.findByAccountIdAndType(accountId, RequestType.BDR))
                .thenReturn(List.of());

        final RequestCreateValidationResult result = validator.validateAction(accountId);

        assertFalse(result.isAvailable());
    }

    @Test
    void validate_whenFaStatusTrue_thenUnavailable() {
        final long accountId = 1L;

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder()
                        .emitterType(EmitterType.GHGE)
                        .faStatus(true)
                        .build());

        when(requestRepository.findByAccountIdAndType(accountId, RequestType.BDR))
                .thenReturn(List.of());

        final RequestCreateValidationResult result = validator.validateAction(accountId);

        assertFalse(result.isAvailable());
    }

    @Test
    void validate_whenBDRExists_thenUnavailable() {
        final long accountId = 1L;

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder()
                        .emitterType(EmitterType.GHGE)
                        .faStatus(false)
                        .build());

        when(requestRepository.findByAccountIdAndType(accountId, RequestType.BDR))
                .thenReturn(List.of(mock(Request.class)));

        final RequestCreateValidationResult result = validator.validateAction(accountId);

        assertFalse(result.isAvailable());
    }

    @Test
    void validate_whenEligible_thenValidAndAvailable() {
        final long accountId = 1L;

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder()
                        .emitterType(EmitterType.GHGE)
                        .faStatus(false)
                        .build());

        when(requestRepository.findByAccountIdAndType(accountId, RequestType.BDR))
                .thenReturn(List.of());

        when(requestCreateValidatorService.validate(
                accountId,
                validator.getApplicableAccountStatuses(),
                validator.getMutuallyExclusiveRequests()))
                .thenReturn(RequestCreateValidationResult.builder()
                        .valid(true)
                        .isAvailable(true)
                        .build());

        final RequestCreateValidationResult result = validator.validateAction(accountId);

        assertTrue(result.isValid());
        assertTrue(result.isAvailable());
    }
}
