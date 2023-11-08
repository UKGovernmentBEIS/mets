package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceRejectedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermitIssuanceRejectedDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_ISSUANCE_REJECTED);
    }

    @Test
    void constructParams() {
        PermitIssuanceRequestPayload payload = PermitIssuanceRequestPayload.builder()
                .determination(PermitIssuanceRejectDetermination.builder()
                        .type(DeterminationType.REJECTED)
                        .reason("Reason")
                        .officialNotice("Official Notice")
                        .build())
                .build();

        String requestId = "1";
        
        Map<String, Object> result = provider.constructParams(payload, requestId);

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of("officialRefusalLetter", "Official Notice"));
    }
}
