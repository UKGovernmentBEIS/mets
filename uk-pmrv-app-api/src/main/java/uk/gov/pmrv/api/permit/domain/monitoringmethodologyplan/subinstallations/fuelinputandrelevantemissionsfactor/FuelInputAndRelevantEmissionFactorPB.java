package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{(#exist and #methodologyAppliedDescription != null and #methodologyAppliedDescription.trim().length() > 0 and " +
                "#dataSources != null and #dataSources.size() > 0 and #hierarchicalOrder != null) or " +
                "(#exist == false and (#methodologyAppliedDescription == null or #methodologyAppliedDescription.trim().length() == 0) and " +
                "(#dataSources == null or #dataSources.isEmpty()) and #hierarchicalOrder == null and (#supportingFiles == null or #supportingFiles.isEmpty()))}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.importedExportedAmountsDataSource.invalid_input"
)
public class FuelInputAndRelevantEmissionFactorPB extends FuelInputAndRelevantEmissionFactor{

    @NotNull
    private boolean exist;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max = 6)
    private List<@Valid @NotNull FuelInputDataSourcePB> dataSources = new ArrayList<>();



}
