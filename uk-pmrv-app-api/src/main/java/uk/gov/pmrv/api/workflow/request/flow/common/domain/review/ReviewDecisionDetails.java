package uk.gov.pmrv.api.workflow.request.flow.common.domain.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReviewDecisionDetails {

    @Size(max = 10000)
    private String notes;
}
