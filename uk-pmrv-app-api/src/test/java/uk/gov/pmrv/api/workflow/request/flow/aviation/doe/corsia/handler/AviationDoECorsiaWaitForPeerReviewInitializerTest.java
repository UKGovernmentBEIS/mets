package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaWaitForPeerReviewInitializerTest {

    @InjectMocks
    private AviationDoECorsiaWaitForPeerReviewInitializer handler;

    @Test
    void initializePayload() {
        AviationDoECorsia doe = AviationDoECorsia.builder()
            .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                .furtherDetails("furtherDetails")
                .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                .build())
            .build();
        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder()
            .doe(doe)
            .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.AVIATION_DOE_CORSIA_WAIT_FOR_PEER_REVIEW_PAYLOAD);
        assertThat(result).isInstanceOf(AviationDoECorsiaApplicationSubmitRequestTaskPayload.class);
        assertThat(result).isEqualTo(AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_WAIT_FOR_PEER_REVIEW_PAYLOAD).doe(doe).build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getRequestTaskTypes()).containsExactlyInAnyOrder(
            RequestTaskType.AVIATION_DOE_CORSIA_WAIT_FOR_PEER_REVIEW);
    }
}
