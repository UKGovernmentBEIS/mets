package uk.gov.pmrv.api.bulkdownload.core.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.bulkdownload.core.domain.PmrvJwtTokenAction;
import uk.gov.pmrv.api.bulkdownload.core.domain.dto.BulkDownloadResponse;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkDownloadGenerateFileService {

    private final PmrvJwtTokenService  pmrvJwtTokenService;
    private final List<BulkDownloadService> services;

    public FileToken generateBulkDownloadAttachmentToken(String workflow, String period, AppUser appUser) {

        log.info(
            "User {} {} generated BULK DOWNLOAD token for download on workflow [{}], period [{}] at [{}]",
            appUser.getFirstName(),
            appUser.getLastName(),
            workflow,
            period,
            Instant.now()
        );

        Map<PmrvJwtTokenAction, String> claims = new HashMap<>();
        claims.put(PmrvJwtTokenAction.BULK_DOWNLOAD_WORKFLOW,workflow);
        claims.put(PmrvJwtTokenAction.BULK_DOWNLOAD_PERIOD,period);
        claims.put(PmrvJwtTokenAction.BULK_DOWNLOAD_COMPETENT_AUTHORITY, appUser.getCompetentAuthority().toString());
        return pmrvJwtTokenService.generateToken(claims);
    }

    public StreamingResponseBody streamWorkflowPeriodData(String workflow, String period, CompetentAuthorityEnum competentAuthority) {

        BulkDownloadService service = services.stream()
            .filter(h -> h.getWorkflow().equals(workflow))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No service defined for workflow: " + workflow));

        return out -> {
            final ZipOutputStream zipOut = new ZipOutputStream(out);

            service.generateFile(zipOut, period, competentAuthority);

            zipOut.finish();
        };
    }

    public BulkDownloadResponse extractBulkDownloadResponseFromToken(String token) {
        Map<PmrvJwtTokenAction, String> claims =
                pmrvJwtTokenService.resolveTokenClaims(
                        token,
                        Set.of(
                                PmrvJwtTokenAction.BULK_DOWNLOAD_WORKFLOW,
                                PmrvJwtTokenAction.BULK_DOWNLOAD_PERIOD,
                                PmrvJwtTokenAction.BULK_DOWNLOAD_COMPETENT_AUTHORITY
                        )
                );

        String period = claims.get(PmrvJwtTokenAction.BULK_DOWNLOAD_PERIOD);
        String workflow = claims.get(PmrvJwtTokenAction.BULK_DOWNLOAD_WORKFLOW);
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.valueOf(claims.get(PmrvJwtTokenAction.BULK_DOWNLOAD_COMPETENT_AUTHORITY));

        StreamingResponseBody stream = this.streamWorkflowPeriodData(workflow, period, competentAuthority);

        return BulkDownloadResponse.builder()
                .body(stream)
                .filename(buildFilename(period, workflow, competentAuthority)).build();
    }

    private String buildFilename(String period, String workflow, CompetentAuthorityEnum competentAuthority) {
        return "%s %s %s.zip".formatted(
                period,
                workflow,
                competentAuthority.getCode()
        );
    }
}
