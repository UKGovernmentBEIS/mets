package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload extends NERApplicationSubmitToVerifierRequestTaskActionPayload {
}
