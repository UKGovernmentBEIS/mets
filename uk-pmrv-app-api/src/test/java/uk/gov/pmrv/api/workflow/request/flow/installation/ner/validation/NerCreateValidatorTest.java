package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@ExtendWith(MockitoExtension.class)
class NerCreateValidatorTest {
    
    @InjectMocks
    private NerCreateValidator validator;
    
    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;
    
    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Test
    void validate_whenHSE_thenInvalid() {
        final long accountId = 1L;
        
        when(installationAccountQueryService.getAccountDTOById(accountId))
            .thenReturn(InstallationAccountDTO.builder().emitterType(EmitterType.HSE).build());

        final RequestCreateValidationResult result = validator.validateAction(accountId);

        assertFalse(result.isValid());
        assertFalse(result.isAvailable());
    }
    
    @Test
    void validate_whenGHGE_thenValid() {
        final long accountId = 1L;

        when(requestCreateValidatorService.validate(
            accountId,
            validator.getApplicableAccountStatuses(),
            validator.getMutuallyExclusiveRequests())
        ).thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(installationAccountQueryService.getAccountDTOById(accountId))
            .thenReturn(InstallationAccountDTO.builder().emitterType(EmitterType.GHGE).build());

        final RequestCreateValidationResult result = validator.validateAction(accountId);

        assertTrue(result.isValid());
        assertTrue(result.isAvailable());
    }
}
