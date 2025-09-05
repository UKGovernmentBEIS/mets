package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewOverallDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewOverallDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class HSETICompletedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private HSETICompletedDocumentTemplateWorkflowParamsProvider paramsProvider;


    @Test
    void constructParams(){

        HSETIRegulatorReviewOverallDecision overallDecision = HSETIRegulatorReviewOverallDecision
                .builder()
                .type(HSETIRegulatorReviewOverallDecisionType.REJECTED)
                .reason("test")
                .build();

        String requestId = "requestId";
        HSETIRequestPayload payload = HSETIRequestPayload
                .builder()
                .overallDecision(overallDecision)
                .build();

        Map<String, Object> params = paramsProvider.constructParams(payload,requestId);

        assertThat(params).containsExactlyEntriesOf(Map.of("overallDecision",overallDecision));
    }


    @Test
    void getContextActionType(){
        assertThat(paramsProvider.getContextActionType())
                .isEqualTo(DocumentTemplateGenerationContextActionType.HSE_TI_COMPLETED);
    }

}
