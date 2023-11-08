package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class VirReviewedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private VirReviewedDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.VIR_REVIEWED);
    }

    @Test
    void constructParams() {
        final RegulatorReviewResponse regulatorReviewResponse = RegulatorReviewResponse.builder()
                .reportSummary("Report Summary")
                .build();
        final VirRequestPayload payload = VirRequestPayload.builder()
                .payloadType(RequestPayloadType.VIR_REQUEST_PAYLOAD)
                .regulatorReviewResponse(regulatorReviewResponse)
                .build();
        String requestId = "1";

        assertThat(provider.constructParams(payload, requestId))
                .isEqualTo(Map.of("regulatorReviewResponse", regulatorReviewResponse));
    }
}
