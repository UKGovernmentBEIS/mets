package uk.gov.pmrv.api.web.orchestrator.account.installation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.ApprovedInstallationAccountQueryService;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountHeaderInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.service.InstallationAccountPermitQueryOrchestrator;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountPermitQueryOrchestratorTest {

    @InjectMocks
    private InstallationAccountPermitQueryOrchestrator orchestrator;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;
    
    @Mock
    private PermitQueryService permitQueryService;

	@Mock
	private ApprovedInstallationAccountQueryService approvedInstallationAccountQueryService;
    
    @Test
    void getAccountWithPermit() {
    	Long accountId = 1L;
    	String fileDocumentUuid = UUID.randomUUID().toString();

		InstallationAccountDTO account = InstallationAccountDTO.builder()
    			.accountType(AccountType.INSTALLATION)
    			.build();
    	
    	FileInfoDTO fileDocument = FileInfoDTO.builder()
    			.name("permitDoc")
    			.uuid(fileDocumentUuid)
    			.build();
    	
    	PermitDetailsDTO permitDetails = PermitDetailsDTO.builder()
    			.activationDate(LocalDate.of(2000, 1, 1))
    			.id("permitId")
    			.fileDocument(fileDocument)
    			.build();
    	
    	when(installationAccountQueryService.getAccountDTOById(accountId)).thenReturn(account);
    	when(permitQueryService.getPermitDetailsByAccountId(accountId)).thenReturn(Optional.of(permitDetails));
    	
    	InstallationAccountPermitDTO result = orchestrator.getAccountWithPermit(accountId);
    	
    	assertThat(result).isEqualTo(InstallationAccountPermitDTO.builder()
    			.account(account)
    			.permit(permitDetails)
    			.build());
    	
    	verify(installationAccountQueryService, times(1)).getAccountDTOById(accountId);
    	verify(permitQueryService, times(1)).getPermitDetailsByAccountId(accountId);
    }

	@Test
	void getAccountHeaderInfoWithPermitId() {
		Long accountId = 1L;
		String accountName = "accountName";
		AccountType accountType = AccountType.INSTALLATION;
		InstallationAccountStatus status = InstallationAccountStatus.LIVE;
		EmitterType emitterType = EmitterType.GHGE;
		InstallationCategory installationCategory = InstallationCategory.B;
		String permitId = "permit";

		InstallationAccountInfoDTO accountInfo = InstallationAccountInfoDTO.builder()
			.id(accountId)
			.name(accountName)
			.accountType(accountType)
			.status(status)
			.emitterType(emitterType)
			.installationCategory(installationCategory)
			.build();

		when(approvedInstallationAccountQueryService.getApprovedAccountById(accountId)).thenReturn(Optional.of(accountInfo));
		when(permitQueryService.getPermitIdByAccountId(accountId)).thenReturn(Optional.of(permitId));

		Optional<InstallationAccountHeaderInfoDTO> optionalAccountHeaderInfo = orchestrator.getAccountHeaderInfoWithPermitId(accountId);

		assertThat(optionalAccountHeaderInfo).isNotEmpty();
		InstallationAccountHeaderInfoDTO accountHeaderInfo = optionalAccountHeaderInfo.get();

		assertThat(accountHeaderInfo).isEqualTo(InstallationAccountHeaderInfoDTO.builder()
			.name(accountName)
			.status(status)
			.emitterType(emitterType)
			.installationCategory(installationCategory)
			.permitId(permitId)
			.build());

		verify(approvedInstallationAccountQueryService, times(1)).getApprovedAccountById(accountId);
		verify(permitQueryService, times(1)).getPermitIdByAccountId(accountId);
	}

	@Test
	void getAccountHeaderInfoWithPermitId_return_empty() {
		Long accountId = 1L;

		when(approvedInstallationAccountQueryService.getApprovedAccountById(accountId)).thenReturn(Optional.empty());

		Optional<InstallationAccountHeaderInfoDTO> optionalAccountHeaderInfo = orchestrator.getAccountHeaderInfoWithPermitId(accountId);

		assertThat(optionalAccountHeaderInfo).isEmpty();

		verify(approvedInstallationAccountQueryService, times(1)).getApprovedAccountById(accountId);
		verifyNoInteractions(permitQueryService);
	}
}
