package uk.gov.pmrv.api.workflow.request.flow.common.aer.domain;

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
                @DiscriminatorMapping(schema = AerDataReviewDecision.class, value = "AER_DATA"),
                @DiscriminatorMapping(schema = AerVerificationReportDataReviewDecision.class, value = "VERIFICATION_REPORT_DATA")
        },
        discriminatorProperty = "reviewDataType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "reviewDataType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AerDataReviewDecision.class, name = "AER_DATA"),
        @JsonSubTypes.Type(value = AerVerificationReportDataReviewDecision.class, name = "VERIFICATION_REPORT_DATA"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AerReviewDecision {

    private AerReviewDataType reviewDataType;
}
