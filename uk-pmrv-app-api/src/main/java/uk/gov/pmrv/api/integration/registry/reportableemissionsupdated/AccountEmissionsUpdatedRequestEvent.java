package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated;

import java.time.Year;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountEmissionsUpdatedRequestEvent {

	private Integer registryId;
	private String reportableEmissions;
	private Year reportingYear;
	
}
