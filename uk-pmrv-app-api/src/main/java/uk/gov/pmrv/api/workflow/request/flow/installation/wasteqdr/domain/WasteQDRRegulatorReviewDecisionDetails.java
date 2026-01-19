package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class WasteQDRRegulatorReviewDecisionDetails {

    @Size(max = 10000)
    private String notes;
}
