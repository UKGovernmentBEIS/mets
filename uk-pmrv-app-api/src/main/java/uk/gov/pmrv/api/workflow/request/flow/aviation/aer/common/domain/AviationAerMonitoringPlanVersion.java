package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerMonitoringPlanVersion {

    @NotNull
    private String empId;

    @NotNull
    private LocalDate empApprovalDate;

    @NotNull
    private Integer empConsolidationNumber;
}
