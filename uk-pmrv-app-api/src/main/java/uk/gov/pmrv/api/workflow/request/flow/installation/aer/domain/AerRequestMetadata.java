package uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.reporting.domain.verification.OverallAssessmentType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadataReportable;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Year;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AerRequestMetadata extends RequestMetadata implements RequestMetadataReportable {

    @NotNull
    @PastOrPresent
    private Year year;

    @NotNull
    @Positive
    private BigDecimal emissions;

    private OverallAssessmentType overallAssessmentType;
    
    @NotNull
    private AerInitiatorRequest initiatorRequest;
    
}
