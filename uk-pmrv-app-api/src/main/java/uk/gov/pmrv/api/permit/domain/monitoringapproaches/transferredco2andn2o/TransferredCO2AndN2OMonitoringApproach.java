package uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TransferredCO2AndN2OMonitoringApproach extends PermitMonitoringApproachSection {

    @Valid
    @NotNull
    private ProcedureOptionalForm deductionsToAmountOfTransferredCO2;

    @NotNull
    private MonitoringTransportNetworkApproach monitoringTransportNetworkApproach;

    @Valid
    @NotNull
    private TransportCO2AndN2OPipelineSystems transportCO2AndN2OPipelineSystems;

    @Valid
    @NotNull
    private ProcedureOptionalForm geologicalStorage;
}
