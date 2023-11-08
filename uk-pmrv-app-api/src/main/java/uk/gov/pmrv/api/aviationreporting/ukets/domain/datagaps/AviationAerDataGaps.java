package uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#exist) == (#dataGaps?.size() gt 0)}", message = "aviationAer.dataGaps.exist")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#exist) == (#affectedFlightsPercentage != null)}", message = "aviationAer.dataGaps.affectedFlightsPercentage")
public class AviationAerDataGaps {

    @NotNull
    private Boolean exist;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull @Valid AviationAerDataGap> dataGaps = new ArrayList<>();

    @Digits(integer = Integer.MAX_VALUE, fraction = 1)
    @PositiveOrZero
    private BigDecimal affectedFlightsPercentage;


}
