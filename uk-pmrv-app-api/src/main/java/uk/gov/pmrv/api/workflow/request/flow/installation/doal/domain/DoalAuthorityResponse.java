package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.time.LocalDate;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = DoalGrantAuthorityResponse.class, value = "VALID"),
                @DiscriminatorMapping(schema = DoalGrantAuthorityWithCorrectionsResponse.class, value = "VALID_WITH_CORRECTIONS"),
                @DiscriminatorMapping(schema = DoalRejectAuthorityResponse.class, value = "INVALID"),
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DoalGrantAuthorityResponse.class, name = "VALID"),
        @JsonSubTypes.Type(value = DoalGrantAuthorityWithCorrectionsResponse.class, name = "VALID_WITH_CORRECTIONS"),
        @JsonSubTypes.Type(value = DoalRejectAuthorityResponse.class, name = "INVALID"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class DoalAuthorityResponse {

    @NotNull
    private DoalAuthorityResponseType type;

    @NotNull
    @PastOrPresent
    private LocalDate authorityRespondDate;
}
