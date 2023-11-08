package uk.gov.pmrv.api.account.installation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.TransferCodeStatus;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.AccountContactUpdateService;
import uk.gov.pmrv.api.account.service.LegalEntityService;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountActivationServiceTest {

    @InjectMocks
    private InstallationAccountActivationService service;

    @Mock
    private LegalEntityService legalEntityService;

    @Mock
    private OperatorAuthorityService operatorAuthorityService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;
    
    @Mock
    private AccountContactUpdateService accountContactUpdateService;

    @Mock
    private InstallationAccountStatusService installationAccountStatusService;
    
    @Mock
    private InstallationAccountTransferCodeGenerator transferCodeGenerator;

    @Test
    void activateAccount_new_permit() {
        final Long accountId = 1L;
        String user = "assignee";
        
        LegalEntityDTO legalEntityDTO = LegalEntityDTO.builder().id(1L).name("le").build();
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .name("account")
                .applicationType(ApplicationType.NEW_PERMIT)
                .legalEntity(legalEntityDTO)
                .build();
        
        LegalEntity legalEntity = LegalEntity.builder()
                .id(1L).name("le")
                .status(LegalEntityStatus.PENDING)
                .build();
        InstallationAccount account = InstallationAccount.builder()
                .id(accountId)
                .name("account")
                .legalEntity(legalEntity)
                .build();
        
        LegalEntity activatedLegalEntity = LegalEntity.builder()
                .id(1L).name("le")
                .status(LegalEntityStatus.ACTIVE)
                .build();
        
        when(installationAccountQueryService.getAccountFullInfoById(accountId)).thenReturn(account);
        when(legalEntityService.activateLegalEntity(accountDTO.getLegalEntity())).thenReturn(activatedLegalEntity);
        
        //invoke
        service.activateAccount(accountId, accountDTO, user);
        
        //verify
        verify(installationAccountQueryService, times(1)).validateAccountNameExistence(accountDTO.getName());
        verify(installationAccountQueryService, times(1)).getAccountFullInfoById(accountId);
        verify(legalEntityService, times(1)).activateLegalEntity(accountDTO.getLegalEntity());
        verify(installationAccountStatusService, times(1)).handleInstallationAccountAccepted(accountId);
        
        assertThat(account.getAcceptedDate()).isNotNull();
        assertThat(account.getLegalEntity()).isEqualTo(activatedLegalEntity);
        
        verify(operatorAuthorityService, times(1)).createOperatorAdminAuthority(accountId, user);
        verify(accountContactUpdateService, times(1)).assignUserAsDefaultAccountContactPoint(user, account);
    }
    
    @Test
    void activateAccount_transfer() {
        
        final Long accountId = 1L;
        final String user = "assignee";

        final LegalEntityDTO legalEntityDTO = LegalEntityDTO.builder().id(1L).name("le").build();
        final InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
            .name("account")
            .applicationType(ApplicationType.TRANSFER)
            .legalEntity(legalEntityDTO)
            .build();

        final LegalEntity legalEntity = LegalEntity.builder()
            .id(1L).name("le")
            .status(LegalEntityStatus.PENDING)
            .build();
        final InstallationAccount account = InstallationAccount.builder()
            .id(accountId)
            .name("account")
            .legalEntity(legalEntity)
            .build();

        final LegalEntity activatedLegalEntity = LegalEntity.builder()
            .id(1L).name("le")
            .status(LegalEntityStatus.ACTIVE)
            .build();
        final String transferCode = "123456789";

        when(installationAccountQueryService.getAccountFullInfoById(accountId)).thenReturn(account);
        when(legalEntityService.activateLegalEntity(accountDTO.getLegalEntity())).thenReturn(activatedLegalEntity);
        when(transferCodeGenerator.generate()).thenReturn(transferCode);

        //invoke
        service.activateAccount(accountId, accountDTO, user);

        //verify
        verify(installationAccountQueryService, times(1)).validateAccountNameExistence(accountDTO.getName());
        verify(installationAccountQueryService, times(1)).getAccountFullInfoById(accountId);
        verify(legalEntityService, times(1)).activateLegalEntity(accountDTO.getLegalEntity());
        verify(installationAccountStatusService, times(1)).handleInstallationAccountForTransferAccepted(accountId);
        verify(transferCodeGenerator, times(1)).generate();

        assertThat(account.getAcceptedDate()).isNotNull();
        assertThat(account.getLegalEntity()).isEqualTo(activatedLegalEntity);
        assertThat(account.getTransferCode()).isEqualTo(transferCode);
        assertThat(account.getTransferCodeStatus()).isEqualTo(TransferCodeStatus.ACTIVE);

        verify(operatorAuthorityService, times(1)).createOperatorAdminAuthority(accountId, user);
        verify(accountContactUpdateService, times(1)).assignUserAsDefaultAccountContactPoint(user, account);
    }
    
}
