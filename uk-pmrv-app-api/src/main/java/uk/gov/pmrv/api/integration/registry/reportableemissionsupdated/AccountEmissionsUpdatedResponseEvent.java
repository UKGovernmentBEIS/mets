package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountEmissionsUpdatedResponseEvent {

	private AccountEmissionsUpdatedRequestEvent event;
	private List<String> errors;
	private RegistryResponseStatus outcome;
	
}
