package uk.gov.pmrv.api.mireport.system.common.jsonprovider;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;
import uk.gov.netz.api.mireport.system.EmptyMiReportSystemParams;
import uk.gov.pmrv.api.mireport.system.common.PmrvMiReportResultTypes;

import java.util.List;

@Component
public class PmrvMiReportParamsTypesProvider implements JsonSubTypesProvider {

	@Override
	public List<NamedType> getTypes() {
		return List.of(
				new NamedType(EmptyMiReportSystemParams.class, PmrvMiReportResultTypes.LIST_OF_VERIFICATION_BODY_USERS.toString()),
				new NamedType(EmptyMiReportSystemParams.class, PmrvMiReportResultTypes.LIST_OF_USER_REPORT_ENTRIES.toString())
				);
	}
}
