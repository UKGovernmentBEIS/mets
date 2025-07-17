package uk.gov.pmrv.api.user.operator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperatorUserTokenVerificationResult {

	private String email;
	private OperatorUserTokenVerificationStatus status;
	
}
