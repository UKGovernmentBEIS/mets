package uk.gov.pmrv.api.mireport.common.jsonprovider;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;
import uk.gov.pmrv.api.mireport.common.verificationbodyusers.VerificationBodyUsersMiReportResult;

import java.util.List;

@Component
public class PmrvMiReportResultTypesProvider implements JsonSubTypesProvider {

	@Override
	public List<NamedType> getTypes() {
		return List.of(
				new NamedType(VerificationBodyUsersMiReportResult.class, "LIST_OF_VERIFICATION_BODY_USERS")
				);
	}

}
