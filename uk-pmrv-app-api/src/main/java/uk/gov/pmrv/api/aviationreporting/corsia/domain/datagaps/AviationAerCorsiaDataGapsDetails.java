package uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGap;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#dataGapsPercentageType eq 'LESS_EQUAL_FIVE_PER_CENT') == (#dataGapsPercentage != null)}",
        message = "aviationAer.corsia.dataGaps.dataGapsPercentage")
@SpELExpression(expression = "{(#dataGapsPercentageType eq 'MORE_THAN_FIVE_PER_CENT') == (#dataGaps?.size() gt 0)}",
        message = "aviationAer.corsia.dataGaps.dataGaps")
@SpELExpression(expression = "{(#dataGapsPercentageType eq 'MORE_THAN_FIVE_PER_CENT') == (#affectedFlightsPercentage != null)}",
        message = "aviationAer.corsia.dataGaps.affectedFlightsPercentage")
public class AviationAerCorsiaDataGapsDetails {

    @NotNull
    private AviationAerCorsiaDataGapsPercentageType dataGapsPercentageType;

    @Digits(integer = 1, fraction = 3)
    @DecimalMin(value = "0")
    @DecimalMax(value = "5.000")
    private BigDecimal dataGapsPercentage;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull @Valid AviationAerDataGap> dataGaps = new ArrayList<>();

    @Digits(integer = Integer.MAX_VALUE, fraction = 1)
    @PositiveOrZero
    private BigDecimal affectedFlightsPercentage;

}
