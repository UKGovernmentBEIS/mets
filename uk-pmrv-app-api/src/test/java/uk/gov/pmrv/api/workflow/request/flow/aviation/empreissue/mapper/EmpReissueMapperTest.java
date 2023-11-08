package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpReissueAccountDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpReissueAccountReport;

class EmpReissueMapperTest {

	private final EmpReissueMapper cut = Mappers.getMapper(EmpReissueMapper.class);

    @Test
    void toEmpReissueAccountReport() {
    	EmpReissueAccountDetails accountDetails = EmpReissueAccountDetails.builder()
    			.accountName("accountName1")
    			.empId("empId1")
    			.build();
    	
    	EmpReissueAccountReport result = cut.toEmpReissueAccountReport(accountDetails);
    	
    	assertThat(result).isEqualTo(EmpReissueAccountReport.builder()
    			.accountName("accountName1")
    			.empId("empId1")
    			.build());
    }
    
    @Test
    void toEmpReissueAccountsReports() {
    	EmpReissueAccountDetails accountDetails = EmpReissueAccountDetails.builder()
    			.accountName("accountName1")
    			.empId("empId1")
    			.build();
    	
    	Map<Long, EmpReissueAccountDetails> accountsDetails = Map.of(
    			1L, accountDetails
    			);
    	
    	Map<Long, EmpReissueAccountReport> result = cut.toEmpReissueAccountsReports(accountsDetails);
    	
    	assertThat(result).isEqualTo(
    		Map.of(1L, 
    				EmpReissueAccountReport.builder()
    			.accountName("accountName1")
    			.empId("empId1")
    			.build()));
    }
}
