package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;


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
                @DiscriminatorMapping(schema = ALRGrantAuthorityResponse.class, value = "VALID"),
                @DiscriminatorMapping(schema = ALRGrantAuthorityWithCorrectionsResponse.class, value = "VALID_WITH_CORRECTIONS"),
                @DiscriminatorMapping(schema = ALRRejectAuthorityResponse.class, value = "INVALID"),
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ALRGrantAuthorityResponse.class, name = "VALID"),
        @JsonSubTypes.Type(value = ALRGrantAuthorityWithCorrectionsResponse.class, name = "VALID_WITH_CORRECTIONS"),
        @JsonSubTypes.Type(value = ALRRejectAuthorityResponse.class, name = "INVALID"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class ALRAuthorityResponse {

    @NotNull
    private DoalAuthorityResponseType type;

    @NotNull
    @PastOrPresent
    private LocalDate authorityRespondDate;
}
