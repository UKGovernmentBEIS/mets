package uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class ReissueReport {

	private LocalDate issueDate;
	private boolean succeeded;
	
}
