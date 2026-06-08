package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewDataType;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = NERNerDataRegulatorReviewDecision.class, value = "NER_DATA"),
                @DiscriminatorMapping(schema = NERVerificationReportDataRegulatorReviewDecision.class, value = "VERIFICATION_REPORT_DATA")
        },
        discriminatorProperty = "reviewDataType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "reviewDataType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NERNerDataRegulatorReviewDecision.class, name = "NER_DATA"),
        @JsonSubTypes.Type(value = NERVerificationReportDataRegulatorReviewDecision.class, name = "VERIFICATION_REPORT_DATA"),
})

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NERReviewDecision {

    private NERReviewDataType reviewDataType;

}
