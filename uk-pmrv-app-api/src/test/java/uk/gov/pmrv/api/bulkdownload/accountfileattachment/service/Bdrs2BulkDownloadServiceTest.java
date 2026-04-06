package uk.gov.pmrv.api.bulkdownload.accountfileattachment.service;

import java.util.Collections;
import java.util.List;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.repository.AccountFileAttachmentRepository;
import uk.gov.pmrv.api.authorization.rules.domain.PmrvScope;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Bdrs2BulkDownloadServiceTest {

    @Mock
    private AccountFileAttachmentRepository accountFileAttachmentRepository;

    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    @Mock
    private Bdrs2BulkDownloadGenerateFileService bdrs2BulkDownloadGenerateFileService;

    @Mock
    private AppUser appUser;

    @InjectMocks
    private Bdrs2BulkDownloadService service;

    @Test
    void canBulkDownload_returnsTrue() {
        when(compAuthAuthorizationResourceService
                .hasUserScopeOnResourceSubType(appUser, PmrvScope.BDRS2_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.BDRS2.name()))
                .thenReturn(true);

        assertThat(service.canBulkDownload(appUser)).isTrue();
    }

    @Test
    void canBulkDownload_returnsFalse() {
        when(compAuthAuthorizationResourceService
                .hasUserScopeOnResourceSubType(appUser, PmrvScope.BDRS2_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.BDRS2.name()))
                .thenReturn(false);

        assertThat(service.canBulkDownload(appUser)).isFalse();
    }

    @Test
    void isWorkflowAvailable_returnsTrue() {
        when(compAuthAuthorizationResourceService
            .hasUserScopeOnResourceSubType(appUser, PmrvScope.BDRS2_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.BDRS2.name()))
            .thenReturn(true);
        when(appUser.getCompetentAuthority()).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(accountFileAttachmentRepository.existsByWorkflowAndCompetentAuthority(
                AccountFileAttachmentWorkflow.BDRS2,
                appUser.getCompetentAuthority()
        )).thenReturn(true);

        assertThat(service.isWorkflowAvailable(appUser)).isTrue();
    }

    @Test
    void isWorkflowAvailable_returnsFalse_whenNoPermission() {
        when(compAuthAuthorizationResourceService
            .hasUserScopeOnResourceSubType(appUser, PmrvScope.BDRS2_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.BDRS2.name()))
            .thenReturn(false);

        assertThat(service.isWorkflowAvailable(appUser)).isFalse();
    }

    @Test
    void isWorkflowAvailable_returnsFalse_whenNoFilesExist() {
        when(compAuthAuthorizationResourceService
            .hasUserScopeOnResourceSubType(appUser, PmrvScope.BDRS2_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.BDRS2.name()))
            .thenReturn(true);
        when(appUser.getCompetentAuthority()).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(accountFileAttachmentRepository.existsByWorkflowAndCompetentAuthority(
            AccountFileAttachmentWorkflow.BDRS2, appUser.getCompetentAuthority()
        )).thenReturn(false);

        assertThat(service.isWorkflowAvailable(appUser)).isFalse();
    }

    @Test
    void getAvailablePeriods_returnsValues() {
        List<String> expectedResult = List.of("2026-2030");
        when(compAuthAuthorizationResourceService
            .hasUserScopeOnResourceSubType(appUser, PmrvScope.BDRS2_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.BDRS2.name()))
            .thenReturn(true);
        when(appUser.getCompetentAuthority()).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(accountFileAttachmentRepository.findDistinctPeriodsByWorkflowAndCA(
            AccountFileAttachmentWorkflow.BDRS2,
            appUser.getCompetentAuthority()
        )).thenReturn(expectedResult);

        List<String> result = service.getAvailablePeriods(appUser);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getAvailablePeriods_returnsEmpty_whenNoPermission() {
        when(compAuthAuthorizationResourceService
            .hasUserScopeOnResourceSubType(appUser, PmrvScope.BDRS2_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.BDRS2.name()))
            .thenReturn(false);

        List<String> result = service.getAvailablePeriods(appUser);

        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void getWorkflow_returnsBdrs2() {
        assertThat(service.getWorkflow()).isEqualTo(AccountFileAttachmentWorkflow.BDRS2.name());
    }

    @Test
    void generateFile_delegatesToGenerateFileService() throws Exception {
        ZipOutputStream zipOut = mock(ZipOutputStream.class);

        service.generateFile(zipOut, "2026-2030", CompetentAuthorityEnum.ENGLAND);

        verify(bdrs2BulkDownloadGenerateFileService).generateFile(zipOut, "2026-2030", CompetentAuthorityEnum.ENGLAND);
    }

}
