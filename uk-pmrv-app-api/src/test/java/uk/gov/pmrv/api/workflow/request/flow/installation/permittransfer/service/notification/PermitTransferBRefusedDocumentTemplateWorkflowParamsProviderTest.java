package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitTransferBRefusedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermitTransferBRefusedDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
            DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_B_REFUSED);
    }

    @Test
    void constructParams() {

        final PermitTransferBRequestPayload payload = PermitTransferBRequestPayload.builder()
            .determination(PermitIssuanceRejectDetermination.builder()
                .type(DeterminationType.REJECTED)
                .officialNotice("Official Notice")
                .build())
            .build();
        String requestId = "1";
        
        final Map<String, Object> result = provider.constructParams(payload, requestId);

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
            "officialNotice", "Official Notice"
        ));
    }
}
