package uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
public class VirApplicationSubmitRequestTaskPayload extends VirApplicationRequestTaskPayload {
}