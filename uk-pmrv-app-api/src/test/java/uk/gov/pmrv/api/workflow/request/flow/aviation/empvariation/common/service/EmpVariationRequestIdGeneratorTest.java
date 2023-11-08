package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class EmpVariationRequestIdGeneratorTest {

	@InjectMocks
    private EmpVariationRequestIdGenerator generator;
    
    @Mock
    private RequestSequenceRepository requestSequenceRepository;

	@Test
	void getTypes() {
		assertThat(generator.getTypes()).containsExactlyInAnyOrder(
				RequestType.EMP_VARIATION_UKETS, RequestType.EMP_VARIATION_CORSIA);
	}
    
    @Test
    void getPrefix() {
    	assertThat(generator.getPrefix()).isEqualTo("EMPV");
    }
    
    @Test
    void generate() {
    	Long accountId = 10000L;
    	long currentSequence = 500;
    	RequestParams params = RequestParams.builder()
    			.accountId(accountId)
    			.type(RequestType.EMP_VARIATION_UKETS)
    			.build();
    	
    	RequestSequence requestSequence = RequestSequence.builder()
    			.id(2L)
    			.businessIdentifier(String.valueOf(accountId))
    			.sequence(currentSequence)
    			.type(RequestType.EMP_VARIATION_UKETS)
    			.build();
    	
    	when(requestSequenceRepository.findByBusinessIdentifierAndType(String.valueOf(accountId), RequestType.EMP_VARIATION_UKETS))
    		.thenReturn(Optional.of(requestSequence));
    	
    	String result = generator.generate(params);
    	
    	assertThat(result).isEqualTo("EMPV" + "10000" + "-" + (currentSequence + 1));
    	verify(requestSequenceRepository, times(1)).findByBusinessIdentifierAndType(String.valueOf(accountId), RequestType.EMP_VARIATION_UKETS);
    	ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);  
    	verify(requestSequenceRepository, times(1)).save(requestSequenceCaptor.capture());
    	RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
    	assertThat(requestSequenceCaptured.getSequence()).isEqualTo(currentSequence + 1);
    }
}
