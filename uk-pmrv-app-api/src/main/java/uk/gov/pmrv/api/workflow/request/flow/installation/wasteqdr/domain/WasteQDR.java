package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = """
        {
          (#reportProvided == false
            && #reasonForUnprovided != null
            && #reasonForUnprovided?.trim()?.length() > 0
            && #report == null
            && (#supportingFiles == null || #supportingFiles.isEmpty())
            && (#notes == null || #notes?.trim()?.length() == 0))
          ||
          (#reportProvided == true
            && (#reasonForUnprovided == null || #reasonForUnprovided?.trim()?.length() == 0)
            && #report != null)
        }
        """,
        message = "wasteqdr.operator.submit.invalid_input"
)

public class WasteQDR {

    private Boolean reportProvided;

    private String reasonForUnprovided;

    private UUID report;

    private String notes;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();
}
