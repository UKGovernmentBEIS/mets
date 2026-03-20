package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BDRS2ApplicationPeerReviewInitializerTest {

    @InjectMocks
    private BDRS2ApplicationPeerReviewInitializer handler;

    @Test
    void initializePayload() {
        BDRS2 bdrs2 = BDRS2.builder().build();

        BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder()
                .bdrs2(bdrs2)
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result).isInstanceOf(BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload.class);

        BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload) result;

        assertThat(taskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.BDRS2_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(taskPayload.getBdrs2()).isEqualTo(bdrs2);
    }
}
