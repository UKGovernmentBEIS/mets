package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NerSaveApplicationReviewRequestTaskActionPayload extends RequestTaskActionPayload {

    private UUID newEntrantDataReport;

    private UUID verifierOpinionStatement;

    private UUID monitoringMethodologyPlan;

    private ConfidentialityStatement confidentialityStatement;

    private AdditionalDocuments additionalDocuments;

    @Builder.Default
    private Map<String, Boolean> nerSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
}
