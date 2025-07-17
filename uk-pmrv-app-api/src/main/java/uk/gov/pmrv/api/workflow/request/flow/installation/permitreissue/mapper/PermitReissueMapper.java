package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.mapper;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountReport;

import java.util.Map;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitReissueMapper {

	PermitReissueAccountReport toPermitReissueAccountReport(PermitReissueAccountDetails accountDetails);
	
	Map<Long, PermitReissueAccountReport> toPermitReissueAccountsReports(Map<Long, PermitReissueAccountDetails> accountsDetails);
}
