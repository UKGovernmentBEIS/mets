package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueElements;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#aircraftTypes?.size() gt 0) && (#aircraftTypes.?[T(java.lang.Boolean).TRUE.equals(#this?.isCurrentlyUsed)]?.size() gt 0)} ", message = "emp.emissionSources.aircraftTypes")
@SpELExpression(expression = "{(#aircraftTypes?.size() gt 0) && (#aircraftTypes.?[(#this?.fuelTypes?.size() gt 0 && #this?.fuelTypes?.contains('OTHER'))]?.size() gt 0) == (#otherFuelExplanation != null)} ", message = "emp.emissionSources.fuelTypes.otherFuelExplanation")
public class EmpEmissionSources implements EmpUkEtsSection {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    @UniqueElements
    private Set<@NotNull @Valid AircraftTypeDetails> aircraftTypes = new HashSet<>();

    @Size(max = 10000)
    private String otherFuelExplanation;

    @Size(max = 2000)
    private String multipleFuelConsumptionMethodsExplanation;

    private EmpProcedureForm additionalAircraftMonitoringApproach;

}
