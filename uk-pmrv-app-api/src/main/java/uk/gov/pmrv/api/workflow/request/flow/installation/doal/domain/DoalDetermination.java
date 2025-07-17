package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = DoalClosedDetermination.class, value = "CLOSED"),
                @DiscriminatorMapping(schema = DoalProceedToAuthorityDetermination.class, value = "PROCEED_TO_AUTHORITY"),
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DoalClosedDetermination.class, name = "CLOSED"),
        @JsonSubTypes.Type(value = DoalProceedToAuthorityDetermination.class, name = "PROCEED_TO_AUTHORITY"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class DoalDetermination {

    @NotNull
    private DoalDeterminationType type;

    @NotBlank
    @Size(max = 10000)
    private String reason;
}
