package uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetailsType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InherentReceivingTransferringInstallation {

    @NotNull
    private InherentCO2Direction inherentCO2Direction;

    @NotNull
    private InstallationDetailsType installationDetailsType;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property =
        "installationDetailsType", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = InherentReceivingTransferringInstallationDetails.class, name =
            "INSTALLATION_DETAILS"),
        @JsonSubTypes.Type(value = InherentReceivingTransferringInstallationEmitter.class, name =
            "INSTALLATION_EMITTER"),
    })
    @Valid
    @NotNull
    private InherentReceivingTransferringInstallationDetailsType inherentReceivingTransferringInstallationDetailsType;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private Set<MeasurementInstrumentOwnerType> measurementInstrumentOwnerTypes = new HashSet<>();

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    @NotNull
    private BigDecimal totalEmissions;

}
