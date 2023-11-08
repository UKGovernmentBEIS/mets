package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class VirApplicationSubmitInitializerTest {

    @InjectMocks
    private VirApplicationSubmitInitializer initializer;

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
                .payload(VirRequestPayload.builder()
                        .payloadType(RequestPayloadType.VIR_REQUEST_PAYLOAD)
                        .verificationData(virVerificationData)
                        .build())
                .build();

        final VirApplicationSubmitRequestTaskPayload expected = VirApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.VIR_APPLICATION_SUBMIT_PAYLOAD)
                .verificationData(virVerificationData)
                .build();

        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(VirApplicationSubmitRequestTaskPayload.class).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(RequestTaskType.VIR_APPLICATION_SUBMIT);
    }
}
