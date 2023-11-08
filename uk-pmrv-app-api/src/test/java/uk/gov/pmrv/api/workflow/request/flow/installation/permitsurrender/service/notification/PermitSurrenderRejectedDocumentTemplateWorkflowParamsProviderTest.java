package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationReject;

@ExtendWith(MockitoExtension.class)
public class PermitSurrenderRejectedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermitSurrenderRejectedDocumentTemplateWorkflowParamsProvider provider;
    
    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_REJECTED);
    }
    
    @Test
    void constructParams() {
        PermitSurrenderRequestPayload payload = PermitSurrenderRequestPayload.builder()
                .reviewDetermination(PermitSurrenderReviewDeterminationReject.builder()
                        .officialRefusalLetter("letter")
                        .shouldFeeBeRefundedToOperator(Boolean.TRUE)
                        .build())
                .build();
        String requestId = "1";
        
        Map<String, Object> result = provider.constructParams(payload, requestId);
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "officialRefusalLetter", "letter",
                "shouldFeeBeRefundedToOperator", Boolean.TRUE
                ));
    }
}
