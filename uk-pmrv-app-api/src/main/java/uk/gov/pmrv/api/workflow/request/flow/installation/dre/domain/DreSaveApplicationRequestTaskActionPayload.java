package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain;

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
public class DreSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

	private Dre dre;
	
	private boolean sectionCompleted;
}
