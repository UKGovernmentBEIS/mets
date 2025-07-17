package uk.gov.pmrv.api.mireport.common.jsonprovider;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;
import uk.gov.netz.api.mireport.domain.EmptyMiReportParams;

import java.util.List;

@Component
public class PmrvMiReportParamsTypesProvider implements JsonSubTypesProvider {

	@Override
	public List<NamedType> getTypes() {
		return List.of(
				new NamedType(EmptyMiReportParams.class, "LIST_OF_VERIFICATION_BODY_USERS")
				);
	}
}
