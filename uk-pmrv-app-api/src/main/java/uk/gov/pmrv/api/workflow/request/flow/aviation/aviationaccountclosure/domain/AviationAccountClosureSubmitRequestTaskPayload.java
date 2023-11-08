package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AviationAccountClosureSubmitRequestTaskPayload extends RequestTaskPayload {

	private AviationAccountClosure aviationAccountClosure;
	
}
