package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitReissueAccountDetails {

	private String permitId;
	private String installationName; // accountName
	private String operatorName; // LE name
	
}
