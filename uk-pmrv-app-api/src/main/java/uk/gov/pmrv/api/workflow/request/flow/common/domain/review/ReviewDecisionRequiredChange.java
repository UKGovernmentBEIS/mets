package uk.gov.pmrv.api.workflow.request.flow.common.domain.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReviewDecisionRequiredChange {

    @Size(max = 10000)
    @NotBlank
    private String reason;

    @Builder.Default
    @JsonInclude(Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();
}
