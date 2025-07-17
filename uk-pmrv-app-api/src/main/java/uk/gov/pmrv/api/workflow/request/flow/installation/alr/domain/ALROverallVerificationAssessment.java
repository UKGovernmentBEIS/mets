package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.reporting.domain.verification.OverallAssessmentType;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = ALRVerifiedSatisfactoryOverallVerificationAssessment.class, value = "VERIFIED_AS_SATISFACTORY"),
                @DiscriminatorMapping(schema = ALRVerifiedWithCommentsOverallVerificationAssessment.class, value = "VERIFIED_WITH_COMMENTS"),
                @DiscriminatorMapping(schema = ALRNotVerifiedOverallVerificationAssessment.class, value = "NOT_VERIFIED")
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ALRVerifiedSatisfactoryOverallVerificationAssessment.class, name = "VERIFIED_AS_SATISFACTORY"),
        @JsonSubTypes.Type(value = ALRVerifiedWithCommentsOverallVerificationAssessment.class, name = "VERIFIED_WITH_COMMENTS"),
        @JsonSubTypes.Type(value = ALRNotVerifiedOverallVerificationAssessment.class, name = "NOT_VERIFIED")
})
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class ALROverallVerificationAssessment {

    @NotNull
    private OverallAssessmentType type;
}
