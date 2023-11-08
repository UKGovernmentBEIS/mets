package uk.gov.pmrv.api.reporting.domain.verification;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.reporting.domain.ActivityLevelReport;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AerVerificationReportTest {

    @Test
    void getVerificationReportAttachments_if_attachment_exist() {
        AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
            .verificationData(AerVerificationData.builder()
                .activityLevelReport(ActivityLevelReport.builder()
                    .freeAllocationOfAllowances(true)
                    .file(UUID.randomUUID())
                    .build())
                .build())
            .build();

        assertEquals(aerVerificationReport.getVerificationReportAttachments(),
            Set.of(aerVerificationReport.getVerificationData().getActivityLevelReport().getFile()));
    }

    @Test
    void getVerificationReportAttachments_if_no_attachment_exist() {
        AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
            .verificationData(AerVerificationData.builder()
                .activityLevelReport(ActivityLevelReport.builder()
                    .freeAllocationOfAllowances(false)
                    .build())
                .build())
            .build();

        assertEquals(aerVerificationReport.getVerificationReportAttachments(), Collections.emptySet());
    }
}