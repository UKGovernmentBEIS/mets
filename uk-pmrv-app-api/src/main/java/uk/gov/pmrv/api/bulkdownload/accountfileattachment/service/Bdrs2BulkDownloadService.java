package uk.gov.pmrv.api.bulkdownload.accountfileattachment.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.fileattachment.repository.AccountFileAttachmentRepository;
import uk.gov.pmrv.api.authorization.rules.domain.PmrvScope;
import uk.gov.pmrv.api.bulkdownload.core.service.BulkDownloadService;

import static uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow.BDRS2;

@Slf4j
@Service
@RequiredArgsConstructor
public class Bdrs2BulkDownloadService implements BulkDownloadService {

    private final AccountFileAttachmentRepository accountFileAttachmentRepository;
    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
    private final Bdrs2BulkDownloadGenerateFileService bdrs2BulkDownloadGenerateFileService;

    public boolean canBulkDownload(AppUser appUser) {
        return compAuthAuthorizationResourceService.hasUserScopeOnResourceSubType(
            appUser,
            PmrvScope.BDRS2_BULK_DOWNLOAD,
            BDRS2.name());
    }

    public boolean isWorkflowAvailable(AppUser appUser) {
        return canBulkDownload(appUser) && accountFileAttachmentRepository.existsByWorkflowAndCompetentAuthority(
            BDRS2,
            appUser.getCompetentAuthority()
        );
    }

    public List<String> getAvailablePeriods(AppUser appUser) {
        if (canBulkDownload(appUser)) {
            return accountFileAttachmentRepository
                .findDistinctPeriodsByWorkflowAndCA(BDRS2, appUser.getCompetentAuthority());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void generateFile(ZipOutputStream zipOut, String period,
                             CompetentAuthorityEnum competentAuthority) throws IOException {
        bdrs2BulkDownloadGenerateFileService.generateFile(zipOut, period, competentAuthority);
    }

    public String getWorkflow() {
        return BDRS2.name();
    }

}
