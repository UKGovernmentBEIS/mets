package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
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
@Schema(
    discriminatorMapping = {
        @DiscriminatorMapping(schema = WasteQDRReviewAcceptedDecisionDetails.class, value = "ACCEPTED"),
        @DiscriminatorMapping(schema = WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails.class, value = "OPERATOR_AMENDS_NEEDED")
    },
    discriminatorProperty = "type")
public abstract class WasteQDRRegulatorReviewDecisionDetails {

    @Size(max = 10000)
    private String notes;
}
