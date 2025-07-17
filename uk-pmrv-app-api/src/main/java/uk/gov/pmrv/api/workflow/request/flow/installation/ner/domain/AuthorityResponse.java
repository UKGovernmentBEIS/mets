package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.AuthorityResponseType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Schema(
    discriminatorMapping = {
        @DiscriminatorMapping(schema = GrantAuthorityResponse.class, value = "VALID"),
        @DiscriminatorMapping(schema = GrantAuthorityResponse.class, value = "VALID_WITH_CORRECTIONS"),
        @DiscriminatorMapping(schema = RejectAuthorityResponse.class, value = "INVALID"),
    },
    discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GrantAuthorityResponse.class, name = "VALID"),
    @JsonSubTypes.Type(value = GrantAuthorityResponse.class, name = "VALID_WITH_CORRECTIONS"),
    @JsonSubTypes.Type(value = RejectAuthorityResponse.class, name = "INVALID"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AuthorityResponse {

    @NotNull
    private AuthorityResponseType type;

    @NotNull
    private Boolean monitoringMethodologyPlanApproved;

    @Size(max = 10000)
    private String decisionComments;
    
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();
}
