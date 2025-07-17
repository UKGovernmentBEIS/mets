package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.datagaps;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#secondarySourcesDataGapsExist) == (#secondarySourcesDataGapsConditions != null)}", message = "emp.dataGaps.secondaryDataSourcesDescription")
public class EmpDataGapsCorsia implements EmpCorsiaSection {

    @NotNull
    private Boolean secondarySourcesDataGapsExist;

    @Size(max = 10000)
    private String secondarySourcesDataGapsConditions;

    @NotBlank
    @Size(max = 10000)
    private String secondaryDataSources;

    @NotBlank
    @Size(max = 10000)
    private String dataGaps;
}
