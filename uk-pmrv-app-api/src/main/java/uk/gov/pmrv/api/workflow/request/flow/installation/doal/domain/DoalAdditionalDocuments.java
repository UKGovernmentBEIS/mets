package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{#exist == (#documents?.size() gt 0)}", message = "doal.additionalDocuments.exist")
public class DoalAdditionalDocuments {

    private boolean exist;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> documents;

    @Size(max = 10000)
    private String comment;
}
