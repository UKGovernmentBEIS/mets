package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferAGrantedService;

@ExtendWith(MockitoExtension.class)
class PermitTransferAGrantedServiceTest {

    @InjectMocks
    private PermitTransferAGrantedService service;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationAccountUpdateService installationAccountUpdateService;

    @Test
    void grant() {

        final long accountId = 1L;
        final String requestId = "requestId";
        final Request request = Request.builder().id(requestId).accountId(accountId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.grant(requestId);

        verify(installationAccountUpdateService, times(1)).updateAccountUponTransferAGranted(accountId);
    }
}
