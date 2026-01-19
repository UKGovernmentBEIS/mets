package uk.gov.pmrv.api.bulkdownload.core.service;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.bulkdownload.core.domain.PmrvJwtTokenAction;
import uk.gov.pmrv.api.bulkdownload.core.domain.dto.BulkDownloadResponse;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anySet;

@ExtendWith(MockitoExtension.class)
public class BulkDownloadGenerateFileServiceTest {

    @Mock
    private PmrvJwtTokenService pmrvJwtTokenService;

    @InjectMocks
    private BulkDownloadGenerateFileService bulkDownloadGenerateFileService;

    private BulkDownloadService service1;

    @BeforeEach
    void setUp() {
        service1 = mock(BulkDownloadService.class);
        // Inject mocks into the service
        bulkDownloadGenerateFileService = new BulkDownloadGenerateFileService(pmrvJwtTokenService, Arrays.asList(service1));
    }

    @Test
    void generateBulkDownloadAttachmentToken_buildsCorrectClaims() {
        FileToken expectedToken = mock(FileToken.class);

        when(pmrvJwtTokenService.generateToken(anyMap()))
                .thenReturn(expectedToken);

        FileToken result =
            bulkDownloadGenerateFileService.generateBulkDownloadAttachmentToken(
                    "ALR",
                    "2024",
                    AppUser.builder()
                        .firstName("first name")
                        .lastName("last name")
                        .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                        .build()
                );

        assertThat(result).isSameAs(expectedToken);

        ArgumentCaptor<Map<PmrvJwtTokenAction, String>> captor =
                ArgumentCaptor.forClass(Map.class);

        verify(pmrvJwtTokenService).generateToken(captor.capture());

        Map<PmrvJwtTokenAction, String> claims = captor.getValue();

        assertThat(claims).containsExactlyInAnyOrderEntriesOf(
                Map.of(
                        PmrvJwtTokenAction.BULK_DOWNLOAD_WORKFLOW, "ALR",
                        PmrvJwtTokenAction.BULK_DOWNLOAD_PERIOD, "2024",
                        PmrvJwtTokenAction.BULK_DOWNLOAD_COMPETENT_AUTHORITY, "ENGLAND"
                )
        );
    }

    @Test
    void streamWorkflowPeriodData_throwsWhenNoServiceFound() {
        when(service1.getWorkflow()).thenReturn(AccountFileAttachmentWorkflow.ALR.name());

        assertThatThrownBy(() ->
            bulkDownloadGenerateFileService.streamWorkflowPeriodData(
                        "UNKNOWN",
                        "2024",
                        CompetentAuthorityEnum.ENGLAND
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No service defined for workflow: UNKNOWN");
    }

    @Test
    void extractBulkDownloadResponseFromToken_buildsResponseCorrectly() {
        Map<PmrvJwtTokenAction, String> claims =
                Map.of(
                        PmrvJwtTokenAction.BULK_DOWNLOAD_WORKFLOW, "ALR",
                        PmrvJwtTokenAction.BULK_DOWNLOAD_PERIOD, "2024",
                        PmrvJwtTokenAction.BULK_DOWNLOAD_COMPETENT_AUTHORITY, "ENGLAND"
                );

        when(pmrvJwtTokenService.resolveTokenClaims(
                eq("token"),
                anySet()
        )).thenReturn(claims);

        when(service1.getWorkflow()).thenReturn(AccountFileAttachmentWorkflow.ALR.name());

        BulkDownloadResponse response =
            bulkDownloadGenerateFileService.extractBulkDownloadResponseFromToken("token");

        assertThat(response.getFilename())
                .isEqualTo("2024 ALR " + CompetentAuthorityEnum.ENGLAND.getCode() + ".zip");

        assertThat(response.getBody()).isNotNull();
    }
}
