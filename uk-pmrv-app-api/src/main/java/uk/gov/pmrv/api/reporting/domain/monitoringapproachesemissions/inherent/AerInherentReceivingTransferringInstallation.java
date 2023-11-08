package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.inherent;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AerInherentReceivingTransferringInstallation {

    /**
     * Should be populated only for aerInherentReceivingTransferringInstallation that come from the permit object and are created
     * during the initialization of the aer
     * request payload.
     */
    private String id;

    @NotNull
    @Valid
    private InherentReceivingTransferringInstallation inherentReceivingTransferringInstallation;

}
