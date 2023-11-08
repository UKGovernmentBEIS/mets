package uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#exist) == (#dataGapsDetails != null)}", message = "aviationAer.dataGaps.exist")
public class AviationAerCorsiaDataGaps {

    @NotNull
    private Boolean exist;

    @Valid
    private AviationAerCorsiaDataGapsDetails dataGapsDetails;
}
