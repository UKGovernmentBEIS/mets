package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.mapper;

import java.util.Map;

import org.mapstruct.Mapper;

import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpReissueAccountDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpReissueAccountReport;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpReissueMapper {

	EmpReissueAccountReport toEmpReissueAccountReport(EmpReissueAccountDetails accountDetails);
	
	Map<Long, EmpReissueAccountReport> toEmpReissueAccountsReports(Map<Long, EmpReissueAccountDetails> accountsDetails);
	
}
