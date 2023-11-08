package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.datagaps;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpDataGaps implements EmpUkEtsSection {

    @NotBlank
    @Size(max = 10000)
    private String dataGaps;

    @NotBlank
    @Size(max = 2000)
    private String secondaryDataSources;

    @NotBlank
    @Size(max = 2000)
    private String substituteData;

    @Size(max = 2000)
    private String otherDataGapsTypes;
}
