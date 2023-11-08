package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.mapper;

import java.util.Map;

import org.mapstruct.Mapper;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountReport;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitReissueMapper {

	PermitReissueAccountReport toPermitReissueAccountReport(PermitReissueAccountDetails accountDetails);
	
	Map<Long, PermitReissueAccountReport> toPermitReissueAccountsReports(Map<Long, PermitReissueAccountDetails> accountsDetails);
}
