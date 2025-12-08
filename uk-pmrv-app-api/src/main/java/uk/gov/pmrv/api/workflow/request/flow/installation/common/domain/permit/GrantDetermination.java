package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.SortedMap;
import java.util.TreeMap;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(
        expression = "{#firstYearOfReportingObligation==null or " +
                "(#firstYearOfReportingObligation ge 2021 and #firstYearOfReportingObligation le (T(java.time.Year).now().getValue() + 1))}",
        message = "The first year of reporting obligation must be between 2021 and one year from now")
public abstract class GrantDetermination extends Determination {
    
	@NotNull
    private LocalDate activationDate;

    private Integer firstYearOfReportingObligation;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private SortedMap<String, @Digits(integer = 8, fraction = 1) @Positive BigDecimal> annualEmissionsTargets = new TreeMap<>();
}
