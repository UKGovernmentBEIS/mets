package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitTransferARefusedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermitTransferARefusedDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private RequestService requestService;
    
    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
            DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_A_REFUSED);
    }

    @Test
    void constructParams() {

        final String receiverRequestId = "receiverRequestId";
        final PermitTransferARequestPayload transfererPayload = PermitTransferARequestPayload.builder()
            .relatedRequestId(receiverRequestId)
            .build();
        
        final PermitTransferBRequestPayload receiverPayload = PermitTransferBRequestPayload.builder()
            .determination(PermitIssuanceRejectDetermination.builder()
                .type(DeterminationType.REJECTED)
                .officialNotice("Official Notice")
                .build())
            .build();
        
        String requestId = "1";
        
        when(requestService.findRequestById(receiverRequestId)).thenReturn(Request.builder().payload(receiverPayload).build());
        
        final Map<String, Object> result = provider.constructParams(transfererPayload, requestId);

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
            "officialNotice", "Official Notice"
        ));
    }
}
