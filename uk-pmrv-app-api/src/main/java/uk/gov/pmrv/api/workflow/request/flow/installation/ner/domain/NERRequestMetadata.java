package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.reporting.domain.verification.OverallAssessmentType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERInitiationType;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NERRequestMetadata extends RequestMetadata {

    private NERInitiationType nerInitiationType;

    private OverallAssessmentType overallAssessmentType;
}
