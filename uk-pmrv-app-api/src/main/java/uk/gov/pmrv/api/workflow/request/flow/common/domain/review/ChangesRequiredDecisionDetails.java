package uk.gov.pmrv.api.workflow.request.flow.common.domain.review;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ChangesRequiredDecisionDetails extends ReviewDecisionDetails {

    @NotEmpty
    @Valid
    @Builder.Default
    private List<ReviewDecisionRequiredChange> requiredChanges = new ArrayList<>();

}
