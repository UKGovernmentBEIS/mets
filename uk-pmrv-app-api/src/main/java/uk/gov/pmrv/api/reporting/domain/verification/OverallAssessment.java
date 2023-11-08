package uk.gov.pmrv.api.reporting.domain.verification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = VerifiedSatisfactoryOverallAssessment.class, value = "VERIFIED_AS_SATISFACTORY"),
                @DiscriminatorMapping(schema = VerifiedWithCommentsOverallAssessment.class, value = "VERIFIED_WITH_COMMENTS"),
                @DiscriminatorMapping(schema = NotVerifiedOverallAssessment.class, value = "NOT_VERIFIED")
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VerifiedSatisfactoryOverallAssessment.class, name = "VERIFIED_AS_SATISFACTORY"),
        @JsonSubTypes.Type(value = VerifiedWithCommentsOverallAssessment.class, name = "VERIFIED_WITH_COMMENTS"),
        @JsonSubTypes.Type(value = NotVerifiedOverallAssessment.class, name = "NOT_VERIFIED")
})
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class OverallAssessment {

    @NotNull
    private OverallAssessmentType type;
}
