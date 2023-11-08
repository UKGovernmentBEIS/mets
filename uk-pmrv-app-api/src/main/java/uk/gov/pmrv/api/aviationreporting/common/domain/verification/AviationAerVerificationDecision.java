package uk.gov.pmrv.api.aviationreporting.common.domain.verification;

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
        @DiscriminatorMapping(schema = AviationAerVerifiedSatisfactoryDecision.class, value = "VERIFIED_AS_SATISFACTORY"),
        @DiscriminatorMapping(schema = AviationAerVerifiedSatisfactoryWithCommentsDecision.class, value = "VERIFIED_AS_SATISFACTORY_WITH_COMMENTS"),
        @DiscriminatorMapping(schema = AviationAerNotVerifiedDecision.class, value = "NOT_VERIFIED")
    },
    discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AviationAerVerifiedSatisfactoryDecision.class, name = "VERIFIED_AS_SATISFACTORY"),
    @JsonSubTypes.Type(value = AviationAerVerifiedSatisfactoryWithCommentsDecision.class, name = "VERIFIED_AS_SATISFACTORY_WITH_COMMENTS"),
    @JsonSubTypes.Type(value = AviationAerNotVerifiedDecision.class, name = "NOT_VERIFIED")
})
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AviationAerVerificationDecision {

    @NotNull
    private AviationAerVerificationDecisionType type;
}
