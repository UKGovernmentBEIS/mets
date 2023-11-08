package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class AviationDreUkEtsApplicationPeerReviewInitializerRequestTaskHandlerTest {

    @InjectMocks
    private AviationDreUkEtsApplicationPeerReviewInitializerRequestTaskHandler cut;

    @Test
    void initializePayload() {
        AviationDre dre = AviationDre.builder()
            .determinationReason(AviationDreDeterminationReason.builder()
                .furtherDetails("furtherDetails")
                .type(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
                .build())
            .build();
        AviationDreUkEtsRequestPayload requestPayload = AviationDreUkEtsRequestPayload.builder()
            .dre(dre)
            .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = cut.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(result).isInstanceOf(AviationDreUkEtsApplicationSubmitRequestTaskPayload.class);
        assertThat(result).isEqualTo(AviationDreUkEtsApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW_PAYLOAD).dre(dre).build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(cut.getRequestTaskTypes()).containsExactlyInAnyOrder(
            RequestTaskType.AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW);
    }
}
