package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = BDRBdrDataRegulatorReviewDecision.class, value = "BDR_DATA"),
                @DiscriminatorMapping(schema = BDRVerificationReportDataRegulatorReviewDecision.class, value = "VERIFICATION_REPORT_DATA")
        },
        discriminatorProperty = "reviewDataType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "reviewDataType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BDRBdrDataRegulatorReviewDecision.class, name = "BDR_DATA"),
        @JsonSubTypes.Type(value = BDRVerificationReportDataRegulatorReviewDecision.class, name = "VERIFICATION_REPORT_DATA"),
})

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BDRReviewDecision {

    private BDRReviewDataType reviewDataType;
}
