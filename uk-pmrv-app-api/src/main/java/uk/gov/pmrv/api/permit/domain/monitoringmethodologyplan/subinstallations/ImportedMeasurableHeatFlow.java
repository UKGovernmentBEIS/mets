package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{(#exist and #methodologyAppliedDescription != null and #methodologyAppliedDescription.trim().length() > 0) or " +
                "(#exist == false and " +
                "(#methodologyAppliedDescription == null or #methodologyAppliedDescription?.trim()?.length() == 0) " +
                "and (#supportingFiles == null or #supportingFiles.isEmpty()))}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.importedMeasurableHeatFlow.invalid_input"
)
public class ImportedMeasurableHeatFlow {

    @NotNull
    private boolean exist;

    @Size(max = 10000)
    private String methodologyAppliedDescription;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();
}
