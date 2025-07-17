package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationScope;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Component
public class PermanentCessationDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<PermanentCessationRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMANENT_CESSATION_SUBMITTED;
    }

    @Override
    public Map<String, Object> constructParams(PermanentCessationRequestPayload payload, String requestId) {
        return Map.of(
                "additionalInformation", payload.getPermanentCessation().getAdditionalDetails(),
                    "isWholeInstallation",
                payload.getPermanentCessation().getCessationScope().equals(PermanentCessationScope.WHOLE_INSTALLATION),
                "cessationDate", payload.getPermanentCessation().getCessationDate().format(
                        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ROOT))
        );
    }
}
