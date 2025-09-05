package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ALRApplicationPeerReviewInitializerTest {

    @InjectMocks
    private ALRApplicationPeerReviewInitializer handler;

    @Test
    void initializePayload() {
        ALR alr = ALR.builder().build();

        ALRRequestPayload requestPayload = ALRRequestPayload.builder()
                .alr(alr)
                .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().build())
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.ALR_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(result).isInstanceOf(ALRApplicationRegulatorReviewSubmitRequestTaskPayload.class);
        assertThat(result).usingRecursiveComparison().isEqualTo(ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.ALR_APPLICATION_PEER_REVIEW_PAYLOAD).alr(alr)
                .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().build())
                .alrFileVersion(1)
                .build());
    }
}
