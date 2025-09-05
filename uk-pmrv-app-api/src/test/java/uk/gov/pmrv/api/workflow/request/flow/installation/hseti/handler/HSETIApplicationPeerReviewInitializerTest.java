package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETI;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class HSETIApplicationPeerReviewInitializerTest {

    @InjectMocks
    private HSETIApplicationPeerReviewInitializer handler;

    @Test
    void initializePayload() {
        HSETI hseti = HSETI.builder().build();

        HSETIRequestPayload requestPayload = HSETIRequestPayload.builder()
                .hseti(hseti)
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.HSE_TI_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(result).isInstanceOf(HSETIApplicationRegulatorReviewSubmitRequestTaskPayload.class);
        assertThat(result).isEqualTo(HSETIApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.HSE_TI_APPLICATION_PEER_REVIEW_PAYLOAD).hseti(hseti).build());
    }
}
