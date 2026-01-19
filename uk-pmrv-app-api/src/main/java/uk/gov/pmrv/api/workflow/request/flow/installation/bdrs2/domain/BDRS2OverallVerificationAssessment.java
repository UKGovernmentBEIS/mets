package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

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
                @DiscriminatorMapping(schema = BDRS2VerifiedSatisfactoryOverallVerificationAssessment.class, value = "VERIFIED_AS_SATISFACTORY"),
                @DiscriminatorMapping(schema = BDRS2VerifiedWithCommentsOverallVerificationAssessment.class, value = "VERIFIED_WITH_COMMENTS"),
                @DiscriminatorMapping(schema = BDRS2NotVerifiedOverallVerificationAssessment.class, value = "NOT_VERIFIED")
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BDRS2VerifiedSatisfactoryOverallVerificationAssessment.class, name = "VERIFIED_AS_SATISFACTORY"),
        @JsonSubTypes.Type(value = BDRS2VerifiedWithCommentsOverallVerificationAssessment.class, name = "VERIFIED_WITH_COMMENTS"),
        @JsonSubTypes.Type(value = BDRS2NotVerifiedOverallVerificationAssessment.class, name = "NOT_VERIFIED")
})
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BDRS2OverallVerificationAssessment {

    @NotNull
    private OverallAssessmentType type;
}
