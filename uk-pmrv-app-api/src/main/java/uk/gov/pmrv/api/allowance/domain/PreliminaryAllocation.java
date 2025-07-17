package uk.gov.pmrv.api.allowance.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;

import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#year == null) || (T(java.time.Year).parse(#year).getValue() >= 2021 && T(java.time.Year).parse(#year).getValue() <= 2035)}", 
    message = "allowance.preliminary.allocation.year.range")
public class PreliminaryAllocation implements Comparable<PreliminaryAllocation> {

    @NotNull
    private SubInstallationName subInstallationName;

    @NotNull
    private Year year;

    @NotNull
    @PositiveOrZero
    private Integer allowances;

    @Override
    public int compareTo(PreliminaryAllocation allocation) {
        int res = this.year.compareTo(allocation.getYear());
        if(res == 0) {
            return this.getSubInstallationName().name().compareTo(allocation.getSubInstallationName().name());
        }
        return res;
    }
}
