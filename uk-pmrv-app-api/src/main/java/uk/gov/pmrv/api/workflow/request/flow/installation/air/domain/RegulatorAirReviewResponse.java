package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegulatorAirReviewResponse {

    @NotEmpty
    @Builder.Default
    private Map<Integer, @NotNull @Valid RegulatorAirImprovementResponse> regulatorImprovementResponses = new HashMap<>();

    @NotBlank
    @Size(max = 10000)
    private String reportSummary;
}
