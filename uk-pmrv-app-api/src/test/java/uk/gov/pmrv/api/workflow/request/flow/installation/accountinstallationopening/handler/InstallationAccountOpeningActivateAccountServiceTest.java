package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountActivationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.mapper.InstallationAccountPayloadMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningActivateAccountServiceTest {

    @InjectMocks
    private InstallationAccountOpeningActivateAccountService installationAccountOpeningActivateAccountService;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationAccountActivationService installationAccountActivationService;

    @Mock
    private InstallationAccountPayloadMapper installationAccountPayloadMapper;

    @Test
    void execute() {
        //prepare data
        final String requestId = "1";
        final Long accountId = 1L;
        final String assignee = "user";
        final InstallationAccountPayload accountPayload = new InstallationAccountPayload();
        final Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
                .accountPayload(accountPayload)
                .operatorAssignee(assignee)
                .build())
            .build();
        
        final InstallationAccountDTO accountDTO = new InstallationAccountDTO();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(installationAccountPayloadMapper.toAccountInstallationDTO(accountPayload)).thenReturn(accountDTO);

        //invoke
        installationAccountOpeningActivateAccountService.execute(requestId);

        //verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(installationAccountPayloadMapper, times(1)).toAccountInstallationDTO(accountPayload);
        verify(installationAccountActivationService, times(1)).activateAccount(accountId, accountDTO, assignee);
    }
}
