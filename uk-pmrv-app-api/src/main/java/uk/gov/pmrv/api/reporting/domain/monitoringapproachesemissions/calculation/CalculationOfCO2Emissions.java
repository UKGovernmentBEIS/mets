package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissionSectionWithTransfer;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@SpELExpression(expression = "{(#hasTransfer && #sourceStreamEmissions.?[#this.transfer == null].empty) " +
    "|| (#hasTransfer == false && #sourceStreamEmissions.?[#this.transfer != null].empty)}",
    message = "aer.monitoringApproaches.transfer.exist")
public class CalculationOfCO2Emissions extends AerMonitoringApproachEmissionSectionWithTransfer {

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<CalculationSourceStreamEmission> sourceStreamEmissions = new ArrayList<>();

}
