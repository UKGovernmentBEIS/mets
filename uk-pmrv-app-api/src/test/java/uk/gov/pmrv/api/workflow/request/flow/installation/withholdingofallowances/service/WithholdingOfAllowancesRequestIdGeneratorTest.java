package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesRequestIdGeneratorTest {

	@InjectMocks
    private WithholdingOfAllowancesRequestIdGenerator cut;

	@Test
	void getTypes() {
		assertThat(cut.getTypes()).containsExactly(RequestType.WITHHOLDING_OF_ALLOWANCES);
	}
    
    @Test
    void getPrefix() {
    	assertThat(cut.getPrefix()).isEqualTo("WA");
    }
}
