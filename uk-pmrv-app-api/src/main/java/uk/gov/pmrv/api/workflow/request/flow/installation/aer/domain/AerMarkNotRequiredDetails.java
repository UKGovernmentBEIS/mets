package uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AerMarkNotRequiredDetails {

    @NotEmpty
    private String reason;

}
