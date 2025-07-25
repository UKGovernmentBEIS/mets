package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit;

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
public class MonitoringPlanVersion {

    @NotNull
    private String permitId;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private Integer permitConsolidationNumber;
}
