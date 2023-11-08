package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@ExtendWith(MockitoExtension.class)
class AviationVirApplicationSubmitInitializerTest {

    @InjectMocks
    private AviationVirApplicationSubmitInitializer initializer;

    @Test
    void initializePayload() {

        final VirVerificationData virVerificationData = VirVerificationData.builder()
            .uncorrectedNonConformities(Map.of(
                "A1",
                UncorrectedItem.builder()
                    .explanation("Explanation")
                    .reference("A1")
                    .materialEffect(true)
                    .build()))
            .build();
        final Request request = Request.builder()
            .type(RequestType.VIR)
            .payload(AviationVirRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_VIR_REQUEST_PAYLOAD)
                .verificationData(virVerificationData)
                .build())
            .build();

        final AviationVirApplicationSubmitRequestTaskPayload expected =
            AviationVirApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD)
                .verificationData(virVerificationData)
                .build();

        // Invoke
        final RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(AviationVirApplicationSubmitRequestTaskPayload.class).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
            .containsExactly(RequestTaskType.AVIATION_VIR_APPLICATION_SUBMIT);
    }
}
