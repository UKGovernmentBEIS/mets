package uk.gov.pmrv.api.bulkdownload.accountfileattachment.service;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlrBulkDownloadServiceTest {

    @Mock
    private AccountFileAttachmentRepository accountFileAttachmentRepository;

    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    @Mock
    private AppUser appUser;

    @InjectMocks
    private AlrBulkDownloadService service;

    @Mock
    private AlrBulkDownloadGenerateFileService alrBulkDownloadGenerateFileService;

    @BeforeEach
    void setUp() {
        service = new AlrBulkDownloadService(accountFileAttachmentRepository, compAuthAuthorizationResourceService, alrBulkDownloadGenerateFileService);
    }

    @Test
    void canBulkDownload_returnsTrue() {
        when(compAuthAuthorizationResourceService
                .hasUserScopeOnResourceSubType(appUser, PmrvScope.ALR_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.ALR.name()))
                .thenReturn(true);

        boolean result = service.canBulkDownload(appUser);

        assertThat(result).isTrue();
    }

    @Test
    void isWorkflowAvailable_returnsTrue() {
        when(compAuthAuthorizationResourceService
            .hasUserScopeOnResourceSubType(appUser, PmrvScope.ALR_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.ALR.name()))
            .thenReturn(true);
        when(appUser.getCompetentAuthority()).thenReturn(CompetentAuthorityEnum.ENGLAND);

        when(accountFileAttachmentRepository.existsByWorkflowAndCompetentAuthority(
                AccountFileAttachmentWorkflow.ALR,
                appUser.getCompetentAuthority()
        )).thenReturn(true);

        boolean result = service.isWorkflowAvailable(appUser);

        assertThat(result).isTrue();
    }

    @Test
    void isWorkflowAvailable_returnsFalse() {
        when(compAuthAuthorizationResourceService
            .hasUserScopeOnResourceSubType(appUser, PmrvScope.ALR_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.ALR.name()))
            .thenReturn(false);

        boolean result = service.isWorkflowAvailable(appUser);

        assertThat(result).isFalse();
    }

    @Test
    void getAvailablePeriods_returnsValues() {
        when(compAuthAuthorizationResourceService
            .hasUserScopeOnResourceSubType(appUser, PmrvScope.ALR_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.ALR.name()))
            .thenReturn(true);
        List<String> expectedResult = List.of("2024", "2023");
        when(appUser.getCompetentAuthority()).thenReturn(CompetentAuthorityEnum.ENGLAND);

        when(accountFileAttachmentRepository.findDistinctPeriodsByWorkflowAndCA(
            AccountFileAttachmentWorkflow.ALR,
            appUser.getCompetentAuthority()
        )).thenReturn(expectedResult);

        List<String> result = service.getAvailablePeriods(appUser);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getAvailablePeriods_throwsIfCannotBulkDownload() {
        when(compAuthAuthorizationResourceService
            .hasUserScopeOnResourceSubType(appUser, PmrvScope.ALR_BULK_DOWNLOAD, AccountFileAttachmentWorkflow.ALR.name()))
            .thenReturn(false);

        List<String> result = service.getAvailablePeriods(appUser);

        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void getWorkflow_test() {
        assertThat(service.getWorkflow()).isEqualTo(AccountFileAttachmentWorkflow.ALR.name());
    }

}