package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.Determinateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRejectDetermination;


@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = PermitVariationGrantDetermination.class, value = "GRANTED"),
                @DiscriminatorMapping(schema = PermitVariationRejectDetermination.class, value = "REJECTED"),
                @DiscriminatorMapping(schema = PermitVariationDeemedWithdrawnDetermination.class, value = "DEEMED_WITHDRAWN")
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PermitVariationGrantDetermination.class, name = "GRANTED"),
    @JsonSubTypes.Type(value = PermitVariationRejectDetermination.class, name = "REJECTED"),
    @JsonSubTypes.Type(value = PermitVariationDeemedWithdrawnDetermination.class, name = "DEEMED_WITHDRAWN"),
})
public interface PermitVariationDeterminateable extends Determinateable {

}
