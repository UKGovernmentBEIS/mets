package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{#startDateOfNonCompliance != null}",
        message = "permitNotification.dateOfNonCompliance.exist")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#isTemporary) == (#technicalCapabilityDetails != null) }",
        message = "permitNotification.technicalCapabilityDetails.isTemporary")
public class CessationNotification extends PermitNotification {

    @JsonUnwrapped
    @Valid
    private DateOfNonCompliance dateOfNonCompliance;

    @NotNull
    private Boolean isTemporary;

    @Valid
    private TechnicalCapabilityDetails technicalCapabilityDetails;
}
