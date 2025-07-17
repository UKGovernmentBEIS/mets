package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PermitAcceptedVariationDecisionDetails extends ReviewDecisionDetails {

    @Builder.Default
    private List<@NotBlank @Size(max = 10000) String> variationScheduleItems = new ArrayList<>();
}
