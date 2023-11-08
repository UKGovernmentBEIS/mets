package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#subsidiaryCompanyExist) == (#subsidiaryCompanies?.size() gt 0)}", message = "emp.operatorDetails.subsidiaryCompanies")
public class EmpCorsiaOperatorDetails extends AviationCorsiaOperatorDetails {

    @NotNull
    @Valid
    private ActivitiesDescriptionCorsia activitiesDescription;

    @NotNull
    private Boolean subsidiaryCompanyExist;

    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<@NotNull @Valid SubsidiaryCompanyCorsia> subsidiaryCompanies = new HashSet<>();
}
