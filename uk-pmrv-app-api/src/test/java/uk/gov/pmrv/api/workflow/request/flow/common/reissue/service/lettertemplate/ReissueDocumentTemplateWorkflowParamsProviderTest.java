package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.lettertemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueChangesDetails;

@ExtendWith(MockitoExtension.class)
class ReissueDocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
    private ReissueDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private RequestService requestService;
    
    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.REISSUE);
    }
    
    @Test
    void constructParams() {

        PermitBatchReissueChangesDetails changesDetails = PermitBatchReissueChangesDetails
				.builder()
				.changes(List.of("change 1", "change 2"))
				.changesSummary("Summary")
				.build();


    	ReissueRequestPayload payload = ReissueRequestPayload.builder()
    			.consolidationNumber(10)
                .build();

        ReissueRequestMetadata metadata = ReissueRequestMetadata
                .builder()
                .changesDetails(changesDetails)
                .build();
        
        String requestId = "1";

        Request request = Request.builder().id(requestId).payload(payload).metadata(metadata).build();

        when(requestService.findRequestById("1")).thenReturn(request);
        
        Map<String, Object> result = provider.constructParams(payload, requestId);
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "consolidationNumber", 10,
                "changesDetails", changesDetails
                ));
    }
    
}
