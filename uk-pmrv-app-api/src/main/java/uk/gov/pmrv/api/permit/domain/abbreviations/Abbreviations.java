package uk.gov.pmrv.api.permit.domain.abbreviations;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitSection;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#abbreviationDefinitions?.size() gt 0)}", message = "permit.abbreviations.exist")
public class Abbreviations implements PermitSection {

    private boolean exist;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<AbbreviationDefinition> abbreviationDefinitions;

}
