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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ALRApplicationAmendsSubmitInitializerTest {

    @InjectMocks
    private ALRApplicationAmendsSubmitInitializer initializer;

    @Test
    void initializePayload() {

        Long accountId = 1L;
        String requestId = "ALR00001-2025";

        Request request = Request
                .builder()
                .id(requestId)
                .type(RequestType.ALR)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(ALRRequestPayload
                        .builder()
                        .alr(ALR.builder().build())
                        .build()
                )
                .metadata(ALRRequestMetaData.builder().year(Year.of(2025)).build())
                .build();

        ALRApplicationAmendsSubmitRequestTaskPayload taskPayload = ALRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.ALR_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .alr(((ALRRequestPayload) request.getPayload()).getAlr())
                .alrAttachments(((ALRRequestPayload) request.getPayload()).getAlrAttachments())
                .alrSectionsCompleted(((ALRRequestPayload) request.getPayload()).getAlrSectionsCompleted())
                .alrFileVersion(1)
                .build();

        ALRApplicationAmendsSubmitRequestTaskPayload actualTaskPayload =
                (ALRApplicationAmendsSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.ALR_APPLICATION_AMENDS_SUBMIT);
    }
}
