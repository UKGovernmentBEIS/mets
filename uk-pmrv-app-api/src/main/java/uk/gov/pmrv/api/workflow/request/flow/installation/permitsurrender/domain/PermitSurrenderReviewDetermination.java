package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = PermitSurrenderReviewDeterminationGrant.class, value = "GRANTED"),
                @DiscriminatorMapping(schema = PermitSurrenderReviewDeterminationReject.class, value = "REJECTED"),
                @DiscriminatorMapping(schema = PermitSurrenderReviewDeterminationDeemWithdraw.class, value = "DEEMED_WITHDRAWN")
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PermitSurrenderReviewDeterminationGrant.class, name = "GRANTED"),
    @JsonSubTypes.Type(value = PermitSurrenderReviewDeterminationReject.class, name = "REJECTED"),
    @JsonSubTypes.Type(value = PermitSurrenderReviewDeterminationDeemWithdraw.class, name = "DEEMED_WITHDRAWN")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class PermitSurrenderReviewDetermination {

    private PermitSurrenderReviewDeterminationType type;

    @NotNull
    @Size(max = 10000)
    private String reason;
}
