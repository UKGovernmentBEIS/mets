package uk.gov.pmrv.api.reporting.domain.prtr;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{#exist == (#codes?.size() gt 0)}", message = "aer.prtrcodes.exist")
public class PRTRCodes {

    private boolean exist;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<PRTRCode> codes = new HashSet<>();
}
