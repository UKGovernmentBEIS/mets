package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RequestVerificationServiceTest {

    @InjectMocks
    private RequestVerificationService service;

    @Mock
    private VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;

    @Test
    void refreshVerificationReportVBDetails() {
    	final Long verificationReportVBId = 1L;
        final Long requestVBId = 2L;
        AerVerificationReport verificationReport = AerVerificationReport.builder()
        		.verificationBodyId(verificationReportVBId)
                .verificationBodyDetails(VerificationBodyDetails.builder()
                        .name("name1")
                        .build())
                .build();
        final VerificationBodyDetails verificationBodyDetailsNew = VerificationBodyDetails.builder()
                .name("name2")
                .build();

        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(verificationReportVBId))
                .thenReturn(Optional.of(verificationBodyDetailsNew));

        // Invoke
        service.refreshVerificationReportVBDetails(verificationReport, requestVBId);

        // Verify
        verify(verificationBodyDetailsQueryService, times(1))
                .getVerificationBodyDetails(verificationReportVBId);
        
        assertThat(verificationReport.getVerificationBodyDetails()).isEqualTo(verificationBodyDetailsNew);
    }
    
    @Test
    void refreshVerificationReportVBDetails_report_with_no_vb_id() {
        final Long requestVBId = 2L;
        AerVerificationReport verificationReport = AerVerificationReport.builder()
                .verificationBodyDetails(VerificationBodyDetails.builder()
                        .name("name1")
                        .build())
                .build();
        final VerificationBodyDetails verificationBodyDetailsNew = VerificationBodyDetails.builder()
                .name("name2")
                .build();

        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId))
                .thenReturn(Optional.of(verificationBodyDetailsNew));

        // Invoke
        service.refreshVerificationReportVBDetails(verificationReport, requestVBId);

        // Verify
        verify(verificationBodyDetailsQueryService, times(1))
                .getVerificationBodyDetails(requestVBId);
        
        assertThat(verificationReport.getVerificationBodyDetails()).isEqualTo(verificationBodyDetailsNew);
    }
    
    @Test
    void refreshVerificationReportVBDetails_report_null() {
        final Long requestVBId = 2L;
        AerVerificationReport verificationReport = null;

        // Invoke
        service.refreshVerificationReportVBDetails(verificationReport, requestVBId);

        // Verify
        verifyNoInteractions(verificationBodyDetailsQueryService);
    }
    
    @Test
    void refreshVerificationReportVBDetails_latestVBDetails_null() {
        final Long requestVBId = 1L;
        final Long verificationReportVBId = 2L;
        AerVerificationReport verificationReport = AerVerificationReport.builder()
        		.verificationBodyId(verificationReportVBId)
                .verificationBodyDetails(VerificationBodyDetails.builder()
                        .name("name1")
                        .build())
                .build();

        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(verificationReportVBId))
                .thenReturn(Optional.empty());

        // Invoke
        service.refreshVerificationReportVBDetails(verificationReport, requestVBId);

        // Verify
        verify(verificationBodyDetailsQueryService, times(1))
                .getVerificationBodyDetails(verificationReportVBId);
        
        assertThat(verificationReport.getVerificationBodyDetails()).isNotNull();
    }
}
