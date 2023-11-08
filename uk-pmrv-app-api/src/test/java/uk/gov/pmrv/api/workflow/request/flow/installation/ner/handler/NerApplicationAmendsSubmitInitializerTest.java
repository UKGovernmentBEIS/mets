package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

@ExtendWith(MockitoExtension.class)
class NerApplicationAmendsSubmitInitializerTest {

    @InjectMocks
    private NerApplicationAmendsSubmitInitializer initializer;

    @Test
    void initializePayload() {

        final AdditionalDocuments documents = AdditionalDocuments.builder().exist(true).documents(Set.of(UUID.randomUUID()))
            .build();
        final Request request = Request.builder().payload(NerRequestPayload.builder()
                .additionalDocuments(documents)
                .build())
            .build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(requestTaskPayload, NerApplicationAmendsSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NER_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
            .additionalDocuments(documents)
            .build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.NER_APPLICATION_AMENDS_SUBMIT);
    }
}
