package uk.gov.pmrv.api.account.installation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountTransferCodeGeneratorTest {

    @InjectMocks
    private InstallationAccountTransferCodeGenerator generator;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Test
    void generate_whenTransferCodeNotExists_thenSuccess() {

        when(installationAccountQueryService.transferCodeExists(any())).thenReturn(false);

        final String transferCode = generator.generate();

        verify(installationAccountQueryService, times(1)).transferCodeExists(any());
        assertThat(transferCode).isNotEmpty();
    }

    @Test
    void generate_whenTransferCodeNotExistsMultipleTries_thenSuccess() {

        when(installationAccountQueryService.transferCodeExists(any())).thenReturn(true).thenReturn(false);

        final String transferCode = generator.generate();

        verify(installationAccountQueryService, times(2)).transferCodeExists(any());
        assertThat(transferCode).isNotEmpty();
    }

    @Test
    void generate_whenTransferCodeExistsMultipleTries_thenFailure() {

        when(installationAccountQueryService.transferCodeExists(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            generator.generate();
        });

        verify(installationAccountQueryService, times(10)).transferCodeExists(any());
    }
}
