package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayloadVerifiable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ALRApplicationSubmitRequestTaskPayload extends ALRApplicationRequestTaskPayload implements RequestTaskPayloadVerifiable {

    private boolean verificationPerformed;
    private Long verificationBodyId;

}
