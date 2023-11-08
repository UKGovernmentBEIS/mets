package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmpOperatorDetails extends AviationOperatorDetails implements EmpUkEtsSection {

    @NotNull
    @Valid
    private ActivitiesDescription activitiesDescription;
}
