package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(
    discriminatorMapping = {
        @DiscriminatorMapping(schema = OperatorAirImprovementYesResponse.class, value = "YES"),
        @DiscriminatorMapping(schema = OperatorAirImprovementNoResponse.class, value = "NO"),
        @DiscriminatorMapping(schema = OperatorAirImprovementAlreadyMadeResponse.class, value = "ALREADY_MADE"),
    },
    discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = OperatorAirImprovementYesResponse.class, name = "YES"),
    @JsonSubTypes.Type(value = OperatorAirImprovementNoResponse.class, name = "NO"),
    @JsonSubTypes.Type(value = OperatorAirImprovementAlreadyMadeResponse.class, name = "ALREADY_MADE"),
})
public abstract class OperatorAirImprovementResponse {
    
    @NotNull
    private AirImprovementResponseType type;

    @JsonIgnore
    public abstract Set<UUID> getAirFiles();
}
