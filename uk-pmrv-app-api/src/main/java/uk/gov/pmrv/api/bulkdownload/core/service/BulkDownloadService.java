package uk.gov.pmrv.api.bulkdownload.core.service;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

public interface BulkDownloadService {

    boolean canBulkDownload(AppUser appUser);

    boolean isWorkflowAvailable(AppUser appUser);

    List<String> getAvailablePeriods(AppUser appUser);

    void generateFile(ZipOutputStream zipOutputStream, String period, CompetentAuthorityEnum competentAuthority) throws IOException;

    String getWorkflow();

}
