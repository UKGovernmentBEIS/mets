package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ALRApplicationSubmitInitializerTest {

    @InjectMocks
    private ALRApplicationSubmitInitializer initializer;


    @Test
    void initializePayload() {

        Long accountId = 1L;
        String requestId = "BDR00001-2025";

        Request request = Request
                .builder()
                .id(requestId)
                .type(RequestType.ALR)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(ALRRequestPayload
                        .builder()
                        .alr(ALR
                                .builder()
                                .build())
                        .build()
                )
                .metadata(ALRRequestMetaData.builder().year(Year.of(2025)).build())
                .build();

        ALRApplicationSubmitRequestTaskPayload taskPayload = ALRApplicationSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                .alr( ((ALRRequestPayload) request.getPayload()).getAlr())
                .alrAttachments( ((ALRRequestPayload) request.getPayload()).getAlrAttachments())
                .alrSectionsCompleted( ((ALRRequestPayload) request.getPayload()).getAlrSectionsCompleted())
                .build();

        ALRApplicationSubmitRequestTaskPayload actualTaskPayload = (ALRApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }

    @Test
    void initializePayload_withVerificationReport_getVerificationBodyId() {

        Long accountId = 1L;
        String requestId = "ALR00001-2025";

        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("test", List.of(true));

        Request request = Request
                .builder()
                .id(requestId)
                .type(RequestType.ALR)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(ALRRequestPayload
                        .builder()
                        .verificationSectionsCompleted(verificationSectionsCompleted)
                        .verificationPerformed(true)
                        .verificationReport(ALRVerificationReport.builder().verificationBodyId(1L).build())
                        .alr(ALR.builder().build())
                        .build()
                )
                .metadata(ALRRequestMetaData.builder().year(Year.of(2025)).build())
                .build();

        ALRApplicationSubmitRequestTaskPayload taskPayload = ALRApplicationSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                .alr(((ALRRequestPayload) request.getPayload()).getAlr())
                .alrAttachments(((ALRRequestPayload) request.getPayload()).getAlrAttachments())
                .verificationBodyId(1L)
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .verificationPerformed(true)
                .alrSectionsCompleted(((ALRRequestPayload) request.getPayload()).getAlrSectionsCompleted())
                .build();

        ALRApplicationSubmitRequestTaskPayload actualTaskPayload =
                (ALRApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }

    @Test
    void initializePayload_withoutVerificationReport_verificationBodyIdNull() {

        Long accountId = 1L;
        String requestId = "ALR00001-2025";

        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("test", List.of(true));

        Request request = Request
                .builder()
                .id(requestId)
                .type(RequestType.ALR)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(ALRRequestPayload
                        .builder()
                        .verificationSectionsCompleted(verificationSectionsCompleted)
                        .verificationPerformed(false)
                        .verificationReport(null)
                        .alr(ALR.builder().build())
                        .build()
                )
                .metadata(ALRRequestMetaData.builder().year(Year.of(2025)).build())
                .build();

        ALRApplicationSubmitRequestTaskPayload taskPayload = ALRApplicationSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                .alr(((ALRRequestPayload) request.getPayload()).getAlr())
                .alrAttachments(((ALRRequestPayload) request.getPayload()).getAlrAttachments())
                .verificationBodyId(null)
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .verificationPerformed(false)
                .alrSectionsCompleted(((ALRRequestPayload) request.getPayload()).getAlrSectionsCompleted())
                .build();

        ALRApplicationSubmitRequestTaskPayload actualTaskPayload =
                (ALRApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }

    @Test
    void getRequestTaskTypes(){
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.ALR_APPLICATION_SUBMIT);
    }
}
