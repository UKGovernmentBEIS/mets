package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class EmpAcceptedVariationDecisionDetails extends ReviewDecisionDetails {

	@Builder.Default
    private List<@NotBlank @Size(max = 10000) String> variationScheduleItems = new ArrayList<>();
}
