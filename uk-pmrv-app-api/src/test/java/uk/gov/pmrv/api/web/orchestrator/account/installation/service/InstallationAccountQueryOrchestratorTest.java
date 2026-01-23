package uk.gov.pmrv.api.web.orchestrator.account.installation.service;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountDetailsDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.ApprovedInstallationAccountQueryService;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountHeaderInfoDTO;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountQueryOrchestratorTest {

    @InjectMocks
    private InstallationAccountQueryOrchestrator orchestrator;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private ApprovedInstallationAccountQueryService approvedInstallationAccountQueryService;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private AccountFileAttachmentService accountFileAttachmentService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Test
    void getAccountWithPermit_returnsAccountAndPermit() {
        Long accountId = 1L;

        InstallationAccountDTO account =
                InstallationAccountDTO.builder().accountType(AccountType.INSTALLATION).build();

        PermitDetailsDTO permit =
                PermitDetailsDTO.builder().id("PERMIT").build();

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(account);
        when(permitQueryService.getPermitDetailsByAccountId(accountId))
                .thenReturn(Optional.of(permit));

        InstallationAccountPermitDTO result =
                orchestrator.getAccountWithPermit(accountId);

        assertThat(result.getAccount()).isEqualTo(account);
        assertThat(result.getPermit()).isEqualTo(permit);
    }

    @Test
    void getAccountWithPermit_noPermit_returnsNullPermit() {
        Long accountId = 1L;

        InstallationAccountDTO account =
                InstallationAccountDTO.builder().accountType(AccountType.INSTALLATION).build();

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(account);
        when(permitQueryService.getPermitDetailsByAccountId(accountId))
                .thenReturn(Optional.empty());

        InstallationAccountPermitDTO result =
                orchestrator.getAccountWithPermit(accountId);

        assertThat(result.getAccount()).isEqualTo(account);
        assertThat(result.getPermit()).isNull();
    }

    @Test
    void getAccountHeaderInfoWithPermitId_returnsHeaderWithPermitId() {
        Long accountId = 1L;

        InstallationAccountInfoDTO accountInfo =
                InstallationAccountInfoDTO.builder()
                        .id(accountId)
                        .name("ACC")
                        .status(InstallationAccountStatus.LIVE)
                        .emitterType(EmitterType.GHGE)
                        .installationCategory(InstallationCategory.B)
                        .build();

        when(approvedInstallationAccountQueryService.getApprovedAccountById(accountId))
                .thenReturn(Optional.of(accountInfo));
        when(permitQueryService.getPermitIdByAccountId(accountId))
                .thenReturn(Optional.of("PERMIT"));

        Optional<InstallationAccountHeaderInfoDTO> result =
                orchestrator.getAccountHeaderInfoWithPermitId(accountId);

        assertThat(result).isPresent();
        assertThat(result.get().getPermitId()).isEqualTo("PERMIT");
    }

    @Test
    void getAccountHeaderInfoWithPermitId_accountNotFound_returnsEmpty() {
        Long accountId = 1L;

        when(approvedInstallationAccountQueryService.getApprovedAccountById(accountId))
                .thenReturn(Optional.empty());

        Optional<InstallationAccountHeaderInfoDTO> result =
                orchestrator.getAccountHeaderInfoWithPermitId(accountId);

        assertThat(result).isEmpty();
        verifyNoInteractions(permitQueryService);
    }

    @Test
    void getAccountDetails_latestAlrAndBdrFileReturned() {
        Long accountId = 1L;

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().build());
        when(permitQueryService.getPermitDetailsByAccountId(accountId))
                .thenReturn(Optional.empty());

        Optional<AccountFileAttachmentDTO> alr =
                Optional.of(AccountFileAttachmentDTO.builder().fileUuid("ALR_FILE123").build());

        Optional<AccountFileAttachmentDTO> bdr =
            Optional.of(AccountFileAttachmentDTO.builder().fileUuid("BDR_FILE123").build());

        when(accountFileAttachmentService
                .getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(
                        accountId,
                        Set.of(AccountFileAttachmentWorkflow.ALR, AccountFileAttachmentWorkflow.DOAL),
                        AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT
                ))
                .thenReturn(alr);

        when(accountFileAttachmentService
            .getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(
                accountId,
                Set.of(AccountFileAttachmentWorkflow.BDR),
                AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT
            ))
            .thenReturn(bdr);

        when(fileAttachmentService.fileAttachmentExist("ALR_FILE123")).thenReturn(true);
        when(fileAttachmentService.fileAttachmentExist("BDR_FILE123")).thenReturn(true);
        when(fileAttachmentService.getFileDTO("ALR_FILE123"))
                .thenReturn(new FileDTO("ALR_FILE123", "ALR_FILE123", null, 1L));
        when(fileAttachmentService.getFileDTO("BDR_FILE123"))
            .thenReturn(new FileDTO("BDR_FILE123", "BDR_FILE123", null, 1L));

        InstallationAccountDetailsDTO result =
                orchestrator.getAccountDetails(accountId);

        assertThat(result.getLatestAlrFile())
                .isEqualTo(FileInfoDTO.builder()
                        .uuid("ALR_FILE123")
                        .name("ALR_FILE123")
                        .build());

        assertThat(result.getLatestBdrFile())
            .isEqualTo(FileInfoDTO.builder()
                .uuid("BDR_FILE123")
                .name("BDR_FILE123")
                .build());
    }

    @Test
    void getAccountDetails_noAlrAttachment_returnsNullLatestAlrAndBdrFile() {
        Long accountId = 1L;

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().build());
        when(permitQueryService.getPermitDetailsByAccountId(accountId))
                .thenReturn(Optional.empty());

        when(accountFileAttachmentService
                .getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(
                        accountId,
                        Set.of(AccountFileAttachmentWorkflow.ALR, AccountFileAttachmentWorkflow.DOAL),
                        AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT
                ))
                .thenReturn(Optional.empty());

        when(accountFileAttachmentService
            .getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(
                accountId,
                Set.of(AccountFileAttachmentWorkflow.BDR),
                AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT
            ))
            .thenReturn(Optional.empty());

        InstallationAccountDetailsDTO result =
                orchestrator.getAccountDetails(accountId);

        assertThat(result.getLatestAlrFile()).isNull();
        assertThat(result.getLatestBdrFile()).isNull();
    }

    @Test
    void getAccountDetails_fileMissing_returnsNullLatestAlrFile() {
        Long accountId = 1L;

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().build());
        when(permitQueryService.getPermitDetailsByAccountId(accountId))
                .thenReturn(Optional.empty());

        Optional<AccountFileAttachmentDTO> alr =
                Optional.of(AccountFileAttachmentDTO.builder().fileUuid("ALR_FILE123").build());

        Optional<AccountFileAttachmentDTO> bdr =
            Optional.of(AccountFileAttachmentDTO.builder().fileUuid("BDR_FILE123").build());

        when(accountFileAttachmentService
                .getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(
                        accountId,
                        Set.of(AccountFileAttachmentWorkflow.ALR, AccountFileAttachmentWorkflow.DOAL),
                        AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT
                ))
                .thenReturn(alr);

        when(accountFileAttachmentService
            .getLatestFinalizedFileByWorkflowsAndWorkflowSubTypeAndAccountId(
                accountId,
                Set.of(AccountFileAttachmentWorkflow.BDR),
                AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT
            ))
            .thenReturn(bdr);

        when(fileAttachmentService.fileAttachmentExist("ALR_FILE123"))
                .thenReturn(false);

        when(fileAttachmentService.fileAttachmentExist("BDR_FILE123"))
            .thenReturn(false);

        InstallationAccountDetailsDTO result =
                orchestrator.getAccountDetails(accountId);

        assertThat(result.getLatestAlrFile()).isNull();
        assertThat(result.getLatestBdrFile()).isNull();
    }

}
