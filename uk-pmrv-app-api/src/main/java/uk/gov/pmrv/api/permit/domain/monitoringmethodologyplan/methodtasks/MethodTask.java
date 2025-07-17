package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.methodtasks;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(
        expression = "{(#physicalPartsAndUnitsAnswer == true && #connections != null && !#connections.isEmpty() " +
                "&& #assignParts != null && #assignParts?.trim()?.length() > 0) || " +
                "((#physicalPartsAndUnitsAnswer == false || #physicalPartsAndUnitsAnswer == null) && (#connections == null || #connections.isEmpty()) " +
                "&& (#assignParts == null || #assignParts?.trim()?.length() == 0))}",
        message = "permit.monitoringmethodologyplans.digitized.methodTasks"
)
public class MethodTask {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Boolean physicalPartsAndUnitsAnswer;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max = 15)
    private List<@Valid @NotNull MethodTaskConnection> connections = new ArrayList<>();

    /**
     * Methods to assign parts of installations
     * and their emissions to sub-installations
     */
    @Size(max = 10000)
    private String assignParts;

    /**
     * Methods used for ensuring that data gaps
     * and double counting are avoided
     */
    @Size(max = 10000)
    private String avoidDoubleCount;

    /**
     * this is used for ui to trigger summary page
     */
    @NotNull
    private Boolean avoidDoubleCountToggle;
}
