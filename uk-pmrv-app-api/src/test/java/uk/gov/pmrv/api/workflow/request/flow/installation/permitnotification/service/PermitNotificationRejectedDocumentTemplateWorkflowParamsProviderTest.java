package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitNotificationRejectedDocumentTemplateWorkflowParamsProviderTest {

    private PermitNotificationCommonDocumentTemplateWorkflowParamsProvider commonParamsProvider;
    private PermitNotificationRejectedDocumentTemplateWorkflowParamsProvider grantedParamsProvider;

    @BeforeEach
    void setUp() {
        commonParamsProvider = mock(PermitNotificationCommonDocumentTemplateWorkflowParamsProvider.class);
        grantedParamsProvider = new PermitNotificationRejectedDocumentTemplateWorkflowParamsProvider(commonParamsProvider);
    }

    @Test
    void getContextActionType_shouldReturn_PERMIT_NOTIFICATION_GRANTED() {
        assertThat(grantedParamsProvider.getContextActionType())
                .isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_REJECTED);
    }

    @Test
    void constructParams_shouldDelegateToCommonParamsProvider() {
        // Arrange
        PermitNotificationRequestPayload payload = mock(PermitNotificationRequestPayload.class);
        String requestId = "REQ-123";
        Map<String, Object> expectedParams = Map.of("key", "value");

        when(commonParamsProvider.constructParams(payload)).thenReturn(expectedParams);

        // Act
        Map<String, Object> result = grantedParamsProvider.constructParams(payload, requestId);

        // Assert
        assertThat(result).isEqualTo(expectedParams);
        verify(commonParamsProvider, times(1)).constructParams(payload);
    }

}
