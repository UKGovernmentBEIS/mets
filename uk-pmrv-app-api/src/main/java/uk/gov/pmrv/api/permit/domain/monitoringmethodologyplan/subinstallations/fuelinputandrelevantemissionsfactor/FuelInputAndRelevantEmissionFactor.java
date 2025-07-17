package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationHierarchicalOrder;
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
                @DiscriminatorMapping(schema = FuelInputAndRelevantEmissionFactorPB.class, value = "PRODUCT_BENCHMARK"),
                @DiscriminatorMapping(schema = FuelInputAndRelevantEmissionFactorFA.class, value = "FALLBACK_APPROACH"),
                @DiscriminatorMapping(schema = FuelInputAndRelevantEmissionFactorHeatFA.class, value = "HEAT_FALLBACK_APPROACH")
        },
        discriminatorProperty = "fuelInputAndRelevantEmissionFactorType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "fuelInputAndRelevantEmissionFactorType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value =  FuelInputAndRelevantEmissionFactorPB.class, name = "PRODUCT_BENCHMARK"),
        @JsonSubTypes.Type(value =  FuelInputAndRelevantEmissionFactorFA.class, name = "FALLBACK_APPROACH"),
        @JsonSubTypes.Type(value =  FuelInputAndRelevantEmissionFactorHeatFA.class, name = "HEAT_FALLBACK_APPROACH")
})
public abstract class FuelInputAndRelevantEmissionFactor {

    @NotNull
    private FuelInputAndRelevantEmissionFactorType fuelInputAndRelevantEmissionFactorType;

    @Size(max = 10000)
    private String methodologyAppliedDescription;

    @Valid
    private SubInstallationHierarchicalOrder hierarchicalOrder;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();

    public static List<SubInstallationType> getEmptyFIandREF_SubInstallationTypes() {
        return List.of(SubInstallationType.PROCESS_EMISSIONS_CL,SubInstallationType.PROCESS_EMISSIONS_NON_CL);
    }
}
