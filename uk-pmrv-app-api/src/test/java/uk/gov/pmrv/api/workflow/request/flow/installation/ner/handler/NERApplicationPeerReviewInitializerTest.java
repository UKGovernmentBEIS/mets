package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class NERApplicationPeerReviewInitializerTest {

    private final NERApplicationPeerReviewInitializer initializer =
            new NERApplicationPeerReviewInitializer();

    @Test
    void initializePayload() {
        NerRequestPayload requestPayload = NerRequestPayload.builder().build();

        Request request = Request.builder()
                .payload(requestPayload)
                .build();

        RequestTaskPayload result = initializer.initializePayload(request);

        assertThat(result).isInstanceOf(NERApplicationRegulatorReviewSubmitRequestTaskPayload.class);

        NERApplicationRegulatorReviewSubmitRequestTaskPayload payload =
                (NERApplicationRegulatorReviewSubmitRequestTaskPayload) result;

        assertThat(payload.getPayloadType())
                .isEqualTo(RequestTaskPayloadType.NER_APPLICATION_PEER_REVIEW_PAYLOAD);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(RequestTaskType.NER_APPLICATION_PEER_REVIEW);
    }
}
