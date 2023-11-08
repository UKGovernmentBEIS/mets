package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.TransferN2O;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#entryAccountingForTransfer) == " +
    "(#installationDetailsType != null)}",
    message = "permit.monitoringapproach.entryAccountingTransfer.exist")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#entryAccountingForTransfer) == (#installationDetailsType != null)}",
    message = "permit.monitoringapproach.entryAccountingTransfer.exist")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#entryAccountingForTransfer) == ((#installationDetailsType eq 'INSTALLATION_EMITTER' && (#installationEmitter != null)) " +
    "|| (#installationDetailsType eq 'INSTALLATION_DETAILS' && (#installationDetails != null)))}",
    message = "permit.monitoringapproach.entryAccountingTransfer.exist")
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "transferType",
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TransferCO2.class, name = "TRANSFER_CO2"),
    @JsonSubTypes.Type(value = TransferN2O.class, name = "TRANSFER_N2O")
})
public abstract class Transfer {

    private Boolean entryAccountingForTransfer;

    private InstallationDetailsType installationDetailsType;

    @NotNull
    private TransferType transferType;

    @Valid
    private InstallationEmitter installationEmitter;

    @Valid
    private InstallationDetails installationDetails;
}
