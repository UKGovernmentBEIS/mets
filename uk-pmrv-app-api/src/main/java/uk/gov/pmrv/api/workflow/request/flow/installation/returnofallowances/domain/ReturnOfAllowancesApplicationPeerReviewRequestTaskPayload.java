package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReturnOfAllowancesApplicationPeerReviewRequestTaskPayload extends RequestTaskPayload {

    @NotNull
    private ReturnOfAllowances returnOfAllowances;

    @Builder.Default
    private Map<String, Boolean> sectionsCompleted = new HashMap<>();
}
