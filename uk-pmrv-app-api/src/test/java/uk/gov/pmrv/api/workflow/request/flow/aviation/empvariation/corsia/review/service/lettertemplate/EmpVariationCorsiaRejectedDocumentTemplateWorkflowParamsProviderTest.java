package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service.lettertemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaRejectedDocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
    private EmpVariationCorsiaRejectedDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
        		DocumentTemplateGenerationContextActionType.EMP_VARIATION_CORSIA_REJECTED);
    }

    @Test
    void constructParams() {
    	String reason = "Rejection reason";
    	EmpVariationCorsiaRequestPayload payload = EmpVariationCorsiaRequestPayload.builder()
                .determination(EmpVariationDetermination.builder()
                        .type(EmpVariationDeterminationType.REJECTED)
                        .reason(reason)
                        .build())
                .build();
    	String requestId = "1";
    	
        Map<String, Object> result = provider.constructParams(payload, requestId);

        assertEquals(result, Map.of("rejectionReason", reason));
    }
}
