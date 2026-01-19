package uk.gov.pmrv.api.bulkdownload.core.domain;

import uk.gov.netz.api.token.JwtTokenAction;


public class PmrvJwtTokenAction extends JwtTokenAction {
    public PmrvJwtTokenAction(String subject, String claimName) {
        super(subject, claimName);
    }

    public static final PmrvJwtTokenAction BULK_DOWNLOAD_WORKFLOW = new PmrvJwtTokenAction("bulk_download", "workflow");
    public static final PmrvJwtTokenAction BULK_DOWNLOAD_PERIOD = new PmrvJwtTokenAction("bulk_download", "period");
    public static final PmrvJwtTokenAction BULK_DOWNLOAD_COMPETENT_AUTHORITY = new PmrvJwtTokenAction("bulk_download", "competent_authority");

}
