package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HSETIRequestMetadata extends RequestMetadata {

    private HSETIAllocationPeriod allocationPeriod;
}
