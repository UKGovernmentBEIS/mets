package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class NERApplicationAmendsSubmitInitializerTest {

    @InjectMocks
    private NERApplicationAmendsSubmitInitializer initializer;

    @Test
    void initializePayload() {
        Long accountId = 1L;
        String requestId = "NER-00001-2025";

        Request request = Request
                .builder()
                .id(requestId)
                .type(RequestType.NER)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(NerRequestPayload
                        .builder()
                        .ner(NER
                                .builder()
                                .build())
                        .build()
                )
                .metadata(NERRequestMetadata.builder().build())
                .build();

        NERApplicationAmendsSubmitRequestTaskPayload taskPayload = NERApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.NER_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .ner(((NerRequestPayload) request.getPayload()).getNer())
                .nerAttachments(((NerRequestPayload) request.getPayload()).getNerAttachments())
                .nerSectionsCompleted(((NerRequestPayload) request.getPayload()).getNerSectionsCompleted())
                .nerFileVersion(((NerRequestPayload) request.getPayload()).getNerFileVersion())
                .build();

        NERApplicationAmendsSubmitRequestTaskPayload actualTaskPayload =
                (NERApplicationAmendsSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.NER_APPLICATION_AMENDS_SUBMIT);
    }
}
