package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityFuelLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityHeatLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityProcessLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels.AnnualProductionLevel;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitNotificationReviewDecision {

    @NotNull
    private PermitNotificationReviewDecisionType type;

    @Valid
    @Schema(
            discriminatorMapping = {
                    @DiscriminatorMapping(schema = PermitNotificationAcceptedDecisionDetails.class, value = "ACCEPTED"),
                    @DiscriminatorMapping(schema = PermitNotificationReviewDecisionDetails.class, value = "REJECTED"),
                    @DiscriminatorMapping(schema = PermitNotificationCompletedDecisionDetails.class, value = "PERMANENT_CESSATION"),
                    @DiscriminatorMapping(schema = PermitNotificationCompletedDecisionDetails.class, value = "TEMPORARY_CESSATION"),
                    @DiscriminatorMapping(schema = PermitNotificationCompletedDecisionDetails.class, value = "CESSATION_TREATED_AS_PERMANENT"),
                    @DiscriminatorMapping(schema = PermitNotificationCompletedDecisionDetails.class, value = "NOT_CESSATION")
            },
            discriminatorProperty = "type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PermitNotificationAcceptedDecisionDetails.class, name = "ACCEPTED"),
            @JsonSubTypes.Type(value = PermitNotificationReviewDecisionDetails.class, name = "REJECTED"),
            @JsonSubTypes.Type(value = PermitNotificationCompletedDecisionDetails.class, name = "PERMANENT_CESSATION"),
            @JsonSubTypes.Type(value = PermitNotificationCompletedDecisionDetails.class, name = "TEMPORARY_CESSATION"),
            @JsonSubTypes.Type(value = PermitNotificationCompletedDecisionDetails.class, name = "CESSATION_TREATED_AS_PERMANENT"),
            @JsonSubTypes.Type(value = PermitNotificationCompletedDecisionDetails.class, name = "NOT_CESSATION")
    })
    private ReviewDecisionDetails details;
}
