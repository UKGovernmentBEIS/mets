package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

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
                @DiscriminatorMapping(schema = BDRS2Bdrs2DataRegulatorReviewDecision.class, value = "BDRS2_DATA"),
                @DiscriminatorMapping(schema = BDRS2VerificationReportDataRegulatorReviewDecision.class, value = "VERIFICATION_REPORT_DATA")
        },
        discriminatorProperty = "reviewDataType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "reviewDataType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BDRS2Bdrs2DataRegulatorReviewDecision.class, name = "BDRS2_DATA"),
        @JsonSubTypes.Type(value = BDRS2VerificationReportDataRegulatorReviewDecision.class, name = "VERIFICATION_REPORT_DATA"),
})

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BDRS2ReviewDecision {

    private BDRS2ReviewDataType reviewDataType;
}
