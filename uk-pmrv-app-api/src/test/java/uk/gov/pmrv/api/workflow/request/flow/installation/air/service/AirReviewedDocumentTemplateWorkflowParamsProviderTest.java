package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirReviewResponse;

@ExtendWith(MockitoExtension.class)
class AirReviewedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private AirReviewedDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.AIR_REVIEWED);
    }

    @Test
    void constructParams() {
        final RegulatorAirReviewResponse regulatorReviewResponse = RegulatorAirReviewResponse.builder()
            .reportSummary("Report Summary")
            .build();
        final AirRequestPayload payload = AirRequestPayload.builder()
            .payloadType(RequestPayloadType.AIR_REQUEST_PAYLOAD)
            .regulatorReviewResponse(regulatorReviewResponse)
            .build();
        String requestId = "1";

        assertThat(provider.constructParams(payload, requestId))
            .isEqualTo(Map.of("regulatorReviewResponse", regulatorReviewResponse));
    }
}
