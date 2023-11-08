package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountRejectionService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningRejectAccountServiceTest {

    @InjectMocks
    private InstallationAccountOpeningRejectAccountService installationAccountOpeningRejectAccountService;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationAccountRejectionService installationAccountRejectionService;

    @Test
    void execute() {
        //prepare data
        final String requestId = "1";
        final Long accountId = 1L;
        final Request request = Request.builder().id(requestId).accountId(accountId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        //invoke
        installationAccountOpeningRejectAccountService.execute(requestId);

        //verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(installationAccountRejectionService, times(1)).rejectAccount(accountId);
    }
}
