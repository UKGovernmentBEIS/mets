package uk.gov.pmrv.api.workflow.request.flow.common.vir.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegulatorReviewResponse {

    @NotEmpty
    @Builder.Default
    private Map<String, @NotNull @Valid RegulatorImprovementResponse> regulatorImprovementResponses = new HashMap<>();

    @NotBlank
    @Size(max = 10000)
    private String reportSummary;
}
