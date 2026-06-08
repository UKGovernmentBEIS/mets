package uk.gov.pmrv.api.mireport.system.common.jsonprovider;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;
import uk.gov.pmrv.api.mireport.system.common.PmrvMiReportResultTypes;
import uk.gov.pmrv.api.mireport.system.common.verificationbodyusers.VerificationBodyUsersMiReportResult;
import uk.gov.pmrv.api.mireport.system.common.userreportentry.UsersMiReportResult;

import java.util.List;

@Component
public class PmrvMiReportResultTypesProvider implements JsonSubTypesProvider {

	@Override
	public List<NamedType> getTypes() {
		return List.of(
				new NamedType(VerificationBodyUsersMiReportResult.class, PmrvMiReportResultTypes.LIST_OF_VERIFICATION_BODY_USERS.toString()),
				new NamedType(UsersMiReportResult.class, PmrvMiReportResultTypes.LIST_OF_USER_REPORT_ENTRIES.toString())
				);
	}

}
