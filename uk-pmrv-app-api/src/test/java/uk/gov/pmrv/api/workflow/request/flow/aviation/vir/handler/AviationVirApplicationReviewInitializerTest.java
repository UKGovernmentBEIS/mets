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
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@ExtendWith(MockitoExtension.class)
class AviationVirApplicationReviewInitializerTest {

    @InjectMocks
    private AviationVirApplicationReviewInitializer initializer;

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
        final Map<String, OperatorImprovementResponse> operatorImprovementResponses = Map.of(
                "A1",
                OperatorImprovementResponse.builder()
                        .isAddressed(false)
                        .addressedDescription("Description")
                        .uploadEvidence(false)
                        .build()
        );
        final Request request = Request.builder()
                .type(RequestType.VIR)
                .payload(AviationVirRequestPayload.builder()
                        .payloadType(RequestPayloadType.AVIATION_VIR_REQUEST_PAYLOAD)
                        .verificationData(virVerificationData)
                        .operatorImprovementResponses(operatorImprovementResponses)
                        .build())
                .build();

        final AviationVirApplicationReviewRequestTaskPayload expected = AviationVirApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD)
                .verificationData(virVerificationData)
                .operatorImprovementResponses(operatorImprovementResponses)
                .build();

        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(AviationVirApplicationReviewRequestTaskPayload.class).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(RequestTaskType.AVIATION_VIR_APPLICATION_REVIEW);
    }
}
