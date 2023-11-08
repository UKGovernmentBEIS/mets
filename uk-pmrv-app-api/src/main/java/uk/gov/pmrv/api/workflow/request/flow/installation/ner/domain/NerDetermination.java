package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerDeterminationType;

@Schema(
    discriminatorMapping = {
        @DiscriminatorMapping(schema = NerEndedDetermination.class, value = "CLOSED"),
        @DiscriminatorMapping(schema = NerEndedDetermination.class, value = "DEEMED_WITHDRAWN"),
        @DiscriminatorMapping(schema = NerProceedToAuthorityDetermination.class, value = "PROCEED_TO_AUTHORITY"),
    },
    discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = NerEndedDetermination.class, name = "CLOSED"),
    @JsonSubTypes.Type(value = NerEndedDetermination.class, name = "DEEMED_WITHDRAWN"),
    @JsonSubTypes.Type(value = NerProceedToAuthorityDetermination.class, name = "PROCEED_TO_AUTHORITY"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class NerDetermination {

    @NotNull
    private NerDeterminationType type;
    
    @NotBlank
    @Size(max = 10000)
    private String reason;
}
