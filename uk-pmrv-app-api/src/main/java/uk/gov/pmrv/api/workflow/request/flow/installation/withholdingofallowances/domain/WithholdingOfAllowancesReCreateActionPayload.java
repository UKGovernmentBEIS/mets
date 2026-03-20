package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class WithholdingOfAllowancesReCreateActionPayload extends RequestCreateActionPayload {

    @NotBlank
    private String requestId;
}
