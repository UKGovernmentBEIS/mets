package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationPeerReviewInitializerTest {

    @InjectMocks
    private BDRApplicationPeerReviewInitializer handler;

    @Test
    void initializePayload() {
        BDR bdr = BDR.builder().build();

        BDRRequestPayload requestPayload = BDRRequestPayload.builder()
                .bdr(bdr)
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.BDR_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(result).isInstanceOf(BDRApplicationRegulatorReviewSubmitRequestTaskPayload.class);
        assertThat(result).isEqualTo(BDRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_PEER_REVIEW_PAYLOAD).bdr(bdr).build());
    }
}
