package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountStatusService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceDeemedWithdrawnService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceDeemedWithdrawnServiceTest {

    @InjectMocks
    private PermitIssuanceDeemedWithdrawnService cut;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationAccountStatusService installationAccountStatusService;

    @Test
    void reject() {

        final String requestId = "1";
        final Request request = Request.builder()
            .id(requestId)
            .accountId(1L)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        cut.deemWithdrawn(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(installationAccountStatusService, times(1)).handlePermitDeemedWithdrawn(request.getAccountId());
    }
}
