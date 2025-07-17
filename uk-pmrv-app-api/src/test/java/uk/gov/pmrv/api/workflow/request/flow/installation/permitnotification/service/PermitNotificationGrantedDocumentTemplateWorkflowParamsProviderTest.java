package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PermitNotificationGrantedDocumentTemplateWorkflowParamsProviderTest {

    private PermitNotificationCommonDocumentTemplateWorkflowParamsProvider commonParamsProvider;
    private PermitNotificationGrantedDocumentTemplateWorkflowParamsProvider grantedParamsProvider;

    @BeforeEach
    void setUp() {
        commonParamsProvider = mock(PermitNotificationCommonDocumentTemplateWorkflowParamsProvider.class);
        grantedParamsProvider = new PermitNotificationGrantedDocumentTemplateWorkflowParamsProvider(commonParamsProvider);
    }

    @Test
    void getContextActionType_shouldReturn_PERMIT_NOTIFICATION_GRANTED() {
        assertThat(grantedParamsProvider.getContextActionType())
                .isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_GRANTED);
    }

    @Test
    void constructParams_shouldDelegateToCommonParamsProvider() {
        PermitNotificationRequestPayload payload = mock(PermitNotificationRequestPayload.class);
        String requestId = "REQ-123";
        Map<String, Object> expectedParams = Map.of("key", "value");

        when(commonParamsProvider.constructParams(payload)).thenReturn(expectedParams);

        Map<String, Object> result = grantedParamsProvider.constructParams(payload, requestId);
        assertThat(result).isEqualTo(expectedParams);
        verify(commonParamsProvider, times(1)).constructParams(payload);
    }
}

