package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceCloseApplicationRequestTaskActionPayload extends RequestTaskActionPayload {
    
    @NotNull
    @Valid
    @JsonUnwrapped
    private NonComplianceCloseJustification closeJustification;
}
