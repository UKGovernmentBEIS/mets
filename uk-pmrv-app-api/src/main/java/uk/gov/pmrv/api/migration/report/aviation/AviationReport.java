package uk.gov.pmrv.api.migration.report.aviation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerVerificationDecisionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationReport {

    @NotNull
    private AviationAccountType accountType;

    @NotNull
    private Long accountId;

    @NotNull
    private Year reportingYear;

    @NotNull
    private String aerId;
    private String aerStatus;
    private LocalDate aerCreatedDate;
    private AviationAerVerificationDecisionType overallAssessmentType;
    private BigDecimal aerApprovedReportableEmissions;

    private String dreId;
    private LocalDate dreCreatedDate;

    private BigDecimal totalEmissionsInternationalFlight;
    private BigDecimal totalEmissionsOffsettingFlights;
    private BigDecimal totalEmissionsClaimedReductions;
}
