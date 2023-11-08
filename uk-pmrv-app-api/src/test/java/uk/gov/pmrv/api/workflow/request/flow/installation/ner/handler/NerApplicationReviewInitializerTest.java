package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

@ExtendWith(MockitoExtension.class)
class NerApplicationReviewInitializerTest {

    @InjectMocks
    private NerApplicationReviewInitializer initializer;

    @Test
    void initializePayload() {

        final Request request = Request.builder().payload(NerRequestPayload.builder().build()).build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(requestTaskPayload, NerApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NER_APPLICATION_REVIEW_PAYLOAD)
            .build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.NER_APPLICATION_REVIEW);
    }
}
