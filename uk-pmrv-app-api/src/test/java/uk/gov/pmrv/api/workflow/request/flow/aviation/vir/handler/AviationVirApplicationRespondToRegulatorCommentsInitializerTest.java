package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@ExtendWith(MockitoExtension.class)
class AviationVirApplicationRespondToRegulatorCommentsInitializerTest {

    @InjectMocks
    private AviationVirApplicationRespondToRegulatorCommentsInitializer initializer;

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
        final RegulatorReviewResponse regulatorReviewResponse = RegulatorReviewResponse.builder()
                .regulatorImprovementResponses(Map.of(
                        "A1", RegulatorImprovementResponse.builder()
                                .improvementRequired(true)
                                .improvementDeadline(LocalDate.now())
                                .build(),
                        "A2", RegulatorImprovementResponse.builder().improvementRequired(false).build()
                ))
                .build();
        final Request request = Request.builder()
                .type(RequestType.AVIATION_VIR)
                .payload(AviationVirRequestPayload.builder()
                        .payloadType(RequestPayloadType.AVIATION_VIR_REQUEST_PAYLOAD)
                        .verificationData(virVerificationData)
                        .operatorImprovementResponses(operatorImprovementResponses)
                        .regulatorReviewResponse(regulatorReviewResponse)
                        .build())
                .build();

        final AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload expected = 
            AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                .verificationData(virVerificationData)
                .operatorImprovementResponses(operatorImprovementResponses)
                .regulatorImprovementResponses(Map.of(
                        "A1", RegulatorImprovementResponse.builder()
                                .improvementRequired(true)
                                .improvementDeadline(LocalDate.now())
                                .build()
                ))
                .build();

        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload.class).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(RequestTaskType.AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS);
    }
}
