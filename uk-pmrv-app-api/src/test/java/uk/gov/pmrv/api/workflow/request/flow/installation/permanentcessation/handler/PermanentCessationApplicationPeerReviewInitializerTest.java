package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PermanentCessationApplicationPeerReviewInitializerTest {

    @InjectMocks
    private PermanentCessationApplicationPeerReviewInitializer handler;

    @Test
    void initializePayload() {
        PermanentCessation permanentCessation = PermanentCessation.builder().build();

        PermanentCessationRequestPayload requestPayload = PermanentCessationRequestPayload.builder()
                .permanentCessation(permanentCessation)
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(result).isInstanceOf(PermanentCessationApplicationSubmitRequestTaskPayload.class);
        assertThat(result).isEqualTo(PermanentCessationApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_PAYLOAD).permanentCessation(permanentCessation).build());
    }
}
