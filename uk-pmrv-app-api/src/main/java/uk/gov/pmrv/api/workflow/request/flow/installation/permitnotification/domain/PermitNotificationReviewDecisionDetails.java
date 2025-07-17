package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Details of the permit notification accept review of regulator superclass")
public class PermitNotificationReviewDecisionDetails extends ReviewDecisionDetails {

    @NotBlank
    @Size(max = 10000)
    private String officialNotice;

}
