package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
public class PermitTransferASaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {
    
    @JsonUnwrapped
    private PermitTransferDetails permitTransferDetails;

    private Boolean sectionCompleted;
}
