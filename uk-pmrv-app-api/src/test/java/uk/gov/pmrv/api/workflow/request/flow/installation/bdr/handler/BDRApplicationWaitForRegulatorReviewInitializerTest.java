package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationWaitForRegulatorReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRInitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationReport;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationWaitForRegulatorReviewInitializerTest {

    @InjectMocks
    private BDRApplicationWaitForRegulatorReviewInitializer initializer;

    @Test
    void initializePayload_bdrInitiated_sendEmailTrue() {
        Long accountId = 1L;
        String requestId = "BDR00001-2025";

        Request request = Request
                 .builder()
                 .id(requestId)
                 .type(RequestType.BDR)
                 .accountId(accountId)
                 .status(RequestStatus.IN_PROGRESS)
                 .payload(BDRRequestPayload
                         .builder()
                         .bdr(BDR
                                 .builder()
                                 .build())
                         .verificationReport(BDRVerificationReport.builder().build())
                         .build()
                 )
                 .metadata(BDRRequestMetadata.builder().bdrInitiationType(BDRInitiationType.INITIATED).year(Year.of(2025)).build())
                 .build();


        BDRApplicationWaitForRegulatorReviewRequestTaskPayload actualTaskPayload =
                (BDRApplicationWaitForRegulatorReviewRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload.isSendEmailNotification()).isTrue();
    }

    @Test
    void initializePayload_bdrReInitiated_sendEmailFalse() {
        Long accountId = 1L;
        String requestId = "BDR00001-2025";

        Request request = Request
                 .builder()
                 .id(requestId)
                 .type(RequestType.BDR)
                 .accountId(accountId)
                 .status(RequestStatus.IN_PROGRESS)
                 .payload(BDRRequestPayload
                         .builder()
                         .bdr(BDR
                                 .builder()
                                 .build())
                         .verificationReport(BDRVerificationReport.builder().build())
                         .build()
                 )
                 .metadata(BDRRequestMetadata.builder().bdrInitiationType(BDRInitiationType.RE_INITIATED).year(Year.of(2025)).build())
                 .build();


        BDRApplicationWaitForRegulatorReviewRequestTaskPayload actualTaskPayload =
                (BDRApplicationWaitForRegulatorReviewRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload.isSendEmailNotification()).isFalse();
    }

    @Test
    void getRequestTaskTypes(){
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.BDR_WAIT_FOR_REGULATOR_REVIEW);
    }
}
