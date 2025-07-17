package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class PermitTransferBValidatorServiceTest {

    @InjectMocks
    private PermitTransferBValidatorService validator;

    @Mock
    private InstallationAccountUpdateService installationAccountUpdateService;
    

    @Test
    void validateAndDisableTransferCodeStatus_whenTransferCodeDisabled_thenException() {

        doThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
            .when(installationAccountUpdateService)
            .disableTransferCodeStatus("123456789");
        
        final BusinessException businessException = assertThrows(BusinessException.class, () ->
            validator.validateAndDisableTransferCodeStatus("123456789"));

        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
    }

    @Test
    void validateAndDisableTransferCodeStatus_whenTransferCodeActive_thenOk() {
        
        doNothing().when(installationAccountUpdateService).disableTransferCodeStatus("123456789");
        
        validator.validateAndDisableTransferCodeStatus("123456789");

        verify(installationAccountUpdateService, times(1)).disableTransferCodeStatus("123456789");
    }
}
