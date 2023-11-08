package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitTransferAAcceptedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermitTransferAAcceptedDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private RequestService requestService;

    @Mock
    private PermitTransferAcceptedCommonDocumentTemplateWorkflowParamsProvider commonProvider;
    
    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
            DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_A_ACCEPTED);
    }

    @Test
    void constructParams() {

        final String receiverRequestId = "receiverRequestId";
        final String transfererRequestId = "transfererRequestId";

        final Request receiverRequest = Request.builder().id(receiverRequestId)
            .payload(PermitTransferBRequestPayload.builder().relatedRequestId(transfererRequestId).build())
            .build();
        final Request transfererRequest = Request.builder().id(transfererRequestId)
            .payload(PermitTransferARequestPayload.builder().relatedRequestId(receiverRequestId).build())
            .build();
        String requestId = "1";

        when(requestService.findRequestById(receiverRequestId)).thenReturn(receiverRequest);
        when(requestService.findRequestById(transfererRequestId)).thenReturn(transfererRequest);

        provider.constructParams(PermitTransferARequestPayload.builder().relatedRequestId(receiverRequestId).build(), requestId);
        
        verify(commonProvider, times(1)).constructParams(receiverRequest, transfererRequest);
    }
}
