package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceDetailsAmendedRequestActionPayload extends RequestActionPayload {

    @NotNull
    private NonComplianceReason reason;

    private LocalDate nonComplianceDate;

    private LocalDate complianceDate;

    @Size(max = 10000)
    private String comments;
}
