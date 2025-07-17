package uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetails;

@NoArgsConstructor
@Data
@EqualsAndHashCode
@SuperBuilder
public class InherentReceivingTransferringInstallationDetails implements InherentReceivingTransferringInstallationDetailsType {

    @NotNull(message = "permit.monitoringapproach.transfer.exist")
    @Valid
    private InstallationDetails installationDetails;

}
