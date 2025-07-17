package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsia3YearPeriodOffsettingWaitForPeerReviewInitializerTest {

    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingWaitForPeerReviewInitializer initializer;

    @Test
    void initializePayload() {
        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting =
                AviationAerCorsia3YearPeriodOffsetting.builder().build();

        AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload = AviationAerCorsia3YearPeriodOffsettingRequestPayload.builder()
                .aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting)
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = initializer.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_WAIT_FOR_PEER_REVIEW_PAYLOAD);
        assertThat(result).isInstanceOf(AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload.class);
        assertThat(result).isEqualTo(AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_WAIT_FOR_PEER_REVIEW_PAYLOAD).aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting).build());
    }


    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_WAIT_FOR_PEER_REVIEW);
    }

}
