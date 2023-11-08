package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = TemporaryFactor.class, value = "TEMPORARY_FACTOR"),
                @DiscriminatorMapping(schema = TemporaryChange.class, value = "TEMPORARY_CHANGE"),
                @DiscriminatorMapping(schema = TemporarySuspension.class, value = "TEMPORARY_SUSPENSION"),
                @DiscriminatorMapping(schema = NonSignificantChange.class, value = "NON_SIGNIFICANT_CHANGE"),
                @DiscriminatorMapping(schema = OtherFactor.class, value = "OTHER_FACTOR"),
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TemporaryFactor.class, name = "TEMPORARY_FACTOR"),
        @JsonSubTypes.Type(value = TemporaryChange.class, name = "TEMPORARY_CHANGE"),
        @JsonSubTypes.Type(value = TemporarySuspension.class, name = "TEMPORARY_SUSPENSION"),
        @JsonSubTypes.Type(value = NonSignificantChange.class, name = "NON_SIGNIFICANT_CHANGE"),
        @JsonSubTypes.Type(value = OtherFactor.class, name = "OTHER_FACTOR")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class PermitNotification {

    private PermitNotificationType type;

    @NotBlank
    @Size(max=10000)
    private String description;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> documents = new HashSet<>();

    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        return Collections.unmodifiableSet(documents);
    }
}
