package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationScope;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PermanentCessationDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermanentCessationDocumentTemplateWorkflowParamsProvider templateWorkflowParamsProvider;

    @Test
    void getContextActionType() {
       assertEquals(DocumentTemplateGenerationContextActionType.PERMANENT_CESSATION_SUBMITTED,
               templateWorkflowParamsProvider.getContextActionType());
    }

    @Test
    void constructParams() {
        String requestId = "test-request-id";
        String additionalDetails = "Test additional details";
        PermanentCessationRequestPayload requestPayload = PermanentCessationRequestPayload.builder()
                .permanentCessation(PermanentCessation.builder()
                        .additionalDetails(additionalDetails)
                        .cessationScope(PermanentCessationScope.WHOLE_INSTALLATION)
                        .cessationDate(LocalDate.of(2025, 1, 1))
                        .build())
                .build();

        Map<String, Object> templateParams = templateWorkflowParamsProvider.constructParams(requestPayload, requestId);

        Map<String, ? extends Serializable> params = Map.of(
                "additionalInformation", additionalDetails,
                "isWholeInstallation", true,
                "cessationDate", "1 Jan 2025");

        assertEquals(templateParams, params);
    }
}