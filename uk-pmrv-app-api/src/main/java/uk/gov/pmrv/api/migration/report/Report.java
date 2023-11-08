package uk.gov.pmrv.api.migration.report;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.reporting.domain.verification.OverallAssessmentType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @NotNull
    private Long accountId;

    @NotNull
    private Year reportingYear;

    @NotNull
    private String aerId;

    @NotNull
    private LocalDate aerCreatedDate;
    
    private String dreId;
    
    private LocalDate dreCreatedDate;
    
    private OverallAssessmentType overallAssessmentType;

    private BigDecimal aerApprovedReportableEmissions;

    private String reportType;
}
