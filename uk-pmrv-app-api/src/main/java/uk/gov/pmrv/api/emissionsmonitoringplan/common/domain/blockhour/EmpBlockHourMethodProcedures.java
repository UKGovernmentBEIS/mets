package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelUpliftSupplierRecordType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#fuelBurnCalculationTypes?.contains('CLEAR_DISTINGUISHION')) == (#clearDistinguishionIcaoAircraftDesignators?.size() gt 0)}", message = "emp.blockHourMethodProcedures.clearDistinguishionIcaoAircraftDesignators")
@SpELExpression(expression = "{(#fuelBurnCalculationTypes?.contains('NOT_CLEAR_DISTINGUISHION')) == (#notClearDistinguishionIcaoAircraftDesignators?.size() gt 0)}", message = "emp.blockHourMethodProcedures.notClearDistinguishionIcaoAircraftDesignators")
@SpELExpression(expression = "{(#fuelBurnCalculationTypes?.contains('CLEAR_DISTINGUISHION')) == (#assignmentAndAdjustment != null)}", message = "emp.blockHourMethodProcedures.assignmentAndAdjustment")
public class EmpBlockHourMethodProcedures implements EmpUkEtsSection, EmpCorsiaSection {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    private Set<@NotNull FuelBurnCalculationType> fuelBurnCalculationTypes = new HashSet<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@NotBlank @Size(max = 20) String> clearDistinguishionIcaoAircraftDesignators = new HashSet<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@NotBlank @Size(max = 20) String> notClearDistinguishionIcaoAircraftDesignators = new HashSet<>();

    @Size(max = 2000)
    private String assignmentAndAdjustment;

    @NotNull
    @Valid
    private EmpProcedureForm blockHoursMeasurement;

    @NotNull
    private FuelUpliftSupplierRecordType fuelUpliftSupplierRecordType;

    @NotNull
    @Valid
    private EmpProcedureForm fuelDensity;

}
