package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.lettertemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRejectDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.lettertemplate.PermitVariationRejectedDocumentTemplateWorkflowParamsProvider;

@ExtendWith(MockitoExtension.class)
class PermitVariationRejectedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermitVariationRejectedDocumentTemplateWorkflowParamsProvider cut;

    @Test
    void getContextActionType() {
        assertThat(cut.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_REJECTED);
    }

    @Test
    void constructParams() {
        PermitVariationRequestPayload payload = PermitVariationRequestPayload.builder()
                .determination(PermitVariationRejectDetermination.builder()
                        .type(DeterminationType.REJECTED)
                        .reason("Reason")
                        .officialNotice("Official Notice")
                        .build())
                .build();
        String requestId = "1";
        
        Map<String, Object> result = cut.constructParams(payload, requestId);

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of("officialNotice", "Official Notice"));
    }
}
