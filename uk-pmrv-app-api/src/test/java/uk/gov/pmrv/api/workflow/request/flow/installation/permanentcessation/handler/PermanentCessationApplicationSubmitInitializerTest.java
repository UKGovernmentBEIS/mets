package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;


import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PermanentCessationApplicationSubmitInitializerTest {

    @InjectMocks
    private PermanentCessationApplicationSubmitInitializer initializer;

    @Test
    void initializePayload() {

        Long accountId = 1L;
        String requestId = "PC00001-2";

        Request request = Request
                .builder()
                .id(requestId)
                .type(RequestType.PERMANENT_CESSATION)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(PermanentCessationRequestPayload
                        .builder()
                        .permanentCessation(PermanentCessation.builder().build())
                        .build()
                )
                .build();

        PermanentCessationApplicationSubmitRequestTaskPayload taskPayload = PermanentCessationApplicationSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.PERMANENT_CESSATION_SUBMIT_PAYLOAD)
                .permanentCessation( ((PermanentCessationRequestPayload) request.getPayload()).getPermanentCessation())
                .permanentCessationAttachments( ((PermanentCessationRequestPayload) request.getPayload()).getPermanentCessationAttachments())
                .permanentCessationSectionsCompleted( ((PermanentCessationRequestPayload) request.getPayload()).getPermanentCessationSectionsCompleted())
                .build();

        PermanentCessationApplicationSubmitRequestTaskPayload actualTaskPayload = (PermanentCessationApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }
}
