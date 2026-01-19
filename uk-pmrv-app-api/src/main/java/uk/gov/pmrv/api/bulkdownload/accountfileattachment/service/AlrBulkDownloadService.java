package uk.gov.pmrv.api.bulkdownload.accountfileattachment.service;

import java.io.IOException;
import java.util.Collections;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.repository.AccountFileAttachmentRepository;
import uk.gov.pmrv.api.authorization.rules.domain.PmrvScope;

import java.util.List;
import uk.gov.pmrv.api.bulkdownload.core.service.BulkDownloadService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlrBulkDownloadService implements BulkDownloadService {

    private final AccountFileAttachmentRepository accountFileAttachmentRepository;
    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
    private final AlrBulkDownloadGenerateFileService alrBulkDownloadGenerateFileService;
    private final AccountFileAttachmentWorkflow WORKFLOW = AccountFileAttachmentWorkflow.ALR;

    public boolean canBulkDownload(AppUser appUser) {
        return compAuthAuthorizationResourceService.hasUserScopeOnResourceSubType(
            appUser,
            PmrvScope.ALR_BULK_DOWNLOAD,
            AccountFileAttachmentWorkflow.ALR.name());
    }

    public boolean isWorkflowAvailable(AppUser appUser) {
        return canBulkDownload(appUser) && accountFileAttachmentRepository.existsByWorkflowAndCompetentAuthority(
            AccountFileAttachmentWorkflow.ALR,
            appUser.getCompetentAuthority()
        );
    }

    public List<String> getAvailablePeriods(AppUser appUser) {
        if (canBulkDownload(appUser)) {
            return accountFileAttachmentRepository
                .findDistinctPeriodsByWorkflowAndCA(WORKFLOW, appUser.getCompetentAuthority());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void generateFile(ZipOutputStream zipOut, String period,
                             CompetentAuthorityEnum competentAuthority) throws IOException {
        alrBulkDownloadGenerateFileService.generateFile(zipOut, period, competentAuthority);
    }

    public String getWorkflow() {
        return WORKFLOW.name();
    }

}
