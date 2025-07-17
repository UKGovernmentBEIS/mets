package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#documents?.size() gt 0)}", message = "emp.additionalDocuments.exist")
public class EmpAdditionalDocuments implements EmpUkEtsSection, EmpCorsiaSection {

    private boolean exist;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> documents;
}
