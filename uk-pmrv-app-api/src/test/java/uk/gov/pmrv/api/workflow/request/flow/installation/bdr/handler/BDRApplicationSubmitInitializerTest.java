package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationReport;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationSubmitInitializerTest {

    @InjectMocks
    private BDRApplicationSubmitInitializer initializer;

    @Test
    void initializePayload() {

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
                         .build()
                 )
                 .metadata(BDRRequestMetadata.builder().year(Year.of(2025)).build())
                 .build();

        BDRApplicationSubmitRequestTaskPayload taskPayload = BDRApplicationSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                .bdr( ((BDRRequestPayload) request.getPayload()).getBdr())
                .bdrAttachments( ((BDRRequestPayload) request.getPayload()).getBdrAttachments())
                .bdrSectionsCompleted( ((BDRRequestPayload) request.getPayload()).getBdrSectionsCompleted())
                .build();

        BDRApplicationSubmitRequestTaskPayload actualTaskPayload = (BDRApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }

    @Test
    void initializePayload_withVerificationReport_getVerificationBodyId() {

        Long accountId = 1L;
        String requestId = "BDR00001-2025";

        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("test",List.of(true));

        Request request = Request
                 .builder()
                 .id(requestId)
                 .type(RequestType.BDR)
                 .accountId(accountId)
                 .status(RequestStatus.IN_PROGRESS)
                 .payload(BDRRequestPayload
                         .builder()
                         .verificationSectionsCompleted(verificationSectionsCompleted)
                         .verificationPerformed(true)
                         .verificationReport(BDRVerificationReport.builder().verificationBodyId(1L).build())
                         .bdr(BDR
                                 .builder()
                                 .build())
                         .build()
                 )
                 .metadata(BDRRequestMetadata.builder().year(Year.of(2025)).build())
                 .build();

        BDRApplicationSubmitRequestTaskPayload taskPayload = BDRApplicationSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                .bdr( ((BDRRequestPayload) request.getPayload()).getBdr())
                .bdrAttachments( ((BDRRequestPayload) request.getPayload()).getBdrAttachments())
                .verificationBodyId(1L)
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .verificationPerformed(true)
                .bdrSectionsCompleted( ((BDRRequestPayload) request.getPayload()).getBdrSectionsCompleted())
                .build();

        BDRApplicationSubmitRequestTaskPayload actualTaskPayload = (BDRApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }


    @Test
    void initializePayload_withoutVerificationReport_verificationBodyIdNull() {

        Long accountId = 1L;
        String requestId = "BDR00001-2025";

        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("test",List.of(true));

        Request request = Request
                 .builder()
                 .id(requestId)
                 .type(RequestType.BDR)
                 .accountId(accountId)
                 .status(RequestStatus.IN_PROGRESS)
                 .payload(BDRRequestPayload
                         .builder()
                         .verificationSectionsCompleted(verificationSectionsCompleted)
                         .verificationPerformed(false)
                         .verificationReport(null)
                         .bdr(BDR
                                 .builder()
                                 .build())
                         .build()
                 )
                 .metadata(BDRRequestMetadata.builder().year(Year.of(2025)).build())
                 .build();

        BDRApplicationSubmitRequestTaskPayload taskPayload = BDRApplicationSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                .bdr( ((BDRRequestPayload) request.getPayload()).getBdr())
                .bdrAttachments( ((BDRRequestPayload) request.getPayload()).getBdrAttachments())
                .verificationBodyId(null)
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .verificationPerformed(false)
                .bdrSectionsCompleted( ((BDRRequestPayload) request.getPayload()).getBdrSectionsCompleted())
                .build();

        BDRApplicationSubmitRequestTaskPayload actualTaskPayload = (BDRApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }


    @Test
    void getRequestTaskTypes(){
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.BDR_APPLICATION_SUBMIT);
    }
}
