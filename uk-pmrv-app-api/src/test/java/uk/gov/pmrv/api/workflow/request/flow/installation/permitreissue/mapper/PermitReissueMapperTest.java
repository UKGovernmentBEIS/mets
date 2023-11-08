package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountReport;

class PermitReissueMapperTest {

	private final PermitReissueMapper cut = Mappers.getMapper(PermitReissueMapper.class);

    @Test
    void toPermitReissueAccountReport() {
    	PermitReissueAccountDetails accountDetails = PermitReissueAccountDetails.builder()
    			.installationName("inst1")
    			.operatorName("oper1")
    			.permitId("permitId1")
    			.build();
    	
    	PermitReissueAccountReport result = cut.toPermitReissueAccountReport(accountDetails);
    	
    	assertThat(result).isEqualTo(PermitReissueAccountReport.builder()
    			.installationName("inst1")
    			.operatorName("oper1")
    			.permitId("permitId1")
    			.build());
    }
    
    @Test
    void toPermitReissueAccountsReports() {
    	PermitReissueAccountDetails accountDetails = PermitReissueAccountDetails.builder()
    			.installationName("inst1")
    			.operatorName("oper1")
    			.permitId("permitId1")
    			.build();
    	
    	Map<Long, PermitReissueAccountDetails> accountsDetails = Map.of(
    			1L, accountDetails
    			);
    	
    	Map<Long, PermitReissueAccountReport> result = cut.toPermitReissueAccountsReports(accountsDetails);
    	
    	assertThat(result).isEqualTo(
    		Map.of(1L, 
    			PermitReissueAccountReport.builder()
    			.installationName("inst1")
    			.operatorName("oper1")
    			.permitId("permitId1")
    			.build()));
    }
}
