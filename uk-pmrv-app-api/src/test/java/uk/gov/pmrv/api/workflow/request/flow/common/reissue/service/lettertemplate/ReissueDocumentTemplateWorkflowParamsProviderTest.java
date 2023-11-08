package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.lettertemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;

@ExtendWith(MockitoExtension.class)
class ReissueDocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
    private ReissueDocumentTemplateWorkflowParamsProvider provider;
    
    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.REISSUE);
    }
    
    @Test
    void constructParams() {
    	ReissueRequestPayload payload = ReissueRequestPayload.builder()
    			.consolidationNumber(10)
                .build();
        
        String requestId = "1";
        
        Map<String, Object> result = provider.constructParams(payload, requestId);
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "consolidationNumber", 10
                ));
    }
    
}
