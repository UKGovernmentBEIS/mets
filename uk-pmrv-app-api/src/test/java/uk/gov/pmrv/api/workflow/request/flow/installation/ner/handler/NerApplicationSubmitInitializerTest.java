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
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

@ExtendWith(MockitoExtension.class)
class NerApplicationSubmitInitializerTest {

    @InjectMocks
    private NerApplicationSubmitInitializer initializer;

    @Test
    void initializePayload() {

        final Request request = Request.builder().payload(NerRequestPayload.builder().ner(NER.builder().build()).build()).build();
        NerRequestPayload nerRequestPayload = (NerRequestPayload) request.getPayload();


        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(requestTaskPayload, NerApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NER_APPLICATION_SUBMIT_PAYLOAD)
            .ner(nerRequestPayload.getNer())
            .nerFileVersion(1)
            .build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.NER_APPLICATION_SUBMIT);
    }
}
