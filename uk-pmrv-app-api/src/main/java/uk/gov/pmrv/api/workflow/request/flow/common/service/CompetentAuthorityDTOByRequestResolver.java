package uk.gov.pmrv.api.workflow.request.flow.common.service;

import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

public interface CompetentAuthorityDTOByRequestResolver {

	CompetentAuthorityDTO resolveCA(Request request);
	
	AccountType getAccountType();
	
}
