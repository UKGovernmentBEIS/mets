package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = DirectlyAttributableEmissionsPB.class, value = "PRODUCT_BENCHMARK"),
                @DiscriminatorMapping(schema = DirectlyAttributableEmissionsFA.class, value = "FALLBACK_APPROACH")
        },
        discriminatorProperty = "directlyAttributableEmissionsType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "directlyAttributableEmissionsType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value =  DirectlyAttributableEmissionsPB.class, name = "PRODUCT_BENCHMARK"),
        @JsonSubTypes.Type(value =  DirectlyAttributableEmissionsFA.class, name = "FALLBACK_APPROACH")
})
public abstract class DirectlyAttributableEmissions {

    @NotNull
    private DirectlyAttributableEmissionsType directlyAttributableEmissionsType;

    @Size(max = 15000)
    private String attribution;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();

    public static List<SubInstallationType> getEmptyDAE_SubInstallationTypes() {
        return List.of(SubInstallationType.PROCESS_EMISSIONS_CL,SubInstallationType.PROCESS_EMISSIONS_NON_CL);
    }
}
