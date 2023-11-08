package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class AviationAccountClosureRequestPayload extends RequestPayload {
	
	private AviationAccountClosure aviationAccountClosure;
	
}
