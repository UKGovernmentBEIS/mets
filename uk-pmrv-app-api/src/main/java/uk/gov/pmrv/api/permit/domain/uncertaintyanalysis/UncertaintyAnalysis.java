package uk.gov.pmrv.api.permit.domain.uncertaintyanalysis;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitSection;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#attachments?.size() gt 0)}", message = "permit.uncertaintyAnalysis.exist")
public class UncertaintyAnalysis implements PermitSection {

    private boolean exist;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Set<UUID> attachments = new HashSet<>();
}
