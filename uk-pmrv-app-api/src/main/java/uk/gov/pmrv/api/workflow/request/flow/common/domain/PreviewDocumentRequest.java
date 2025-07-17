package uk.gov.pmrv.api.workflow.request.flow.common.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviewDocumentRequest {

    @NotNull
    private DocumentTemplateType documentType;

    @NotNull
    @Valid
    private DecisionNotification decisionNotification;
}
