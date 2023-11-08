package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBRequestIdGenerator;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitTransferBRequestIdGeneratorTest {

	@InjectMocks
    private PermitTransferBRequestIdGenerator generator;
    
    @Mock
    private RequestSequenceRepository requestSequenceRepository;

	@Test
	void getTypes() {
		assertThat(generator.getTypes()).containsExactly(RequestType.PERMIT_TRANSFER_B);
	}
    
    @Test
    void getPrefix() {
    	assertThat(generator.getPrefix()).isEqualTo("AEMTB");
    }
    
    @Test
    void generate() {
    	
    	final Long accountId = 1L;
		final long currentSequence = 3;
		final RequestParams params = RequestParams.builder()
    			.accountId(accountId)
    			.type(RequestType.PERMIT_TRANSFER_B)
    			.build();

		final RequestSequence requestSequence = RequestSequence.builder()
    			.id(2L)
    			.businessIdentifier(String.valueOf(accountId))
    			.sequence(currentSequence)
    			.type(RequestType.PERMIT_TRANSFER_B)
    			.build();
    	
    	when(requestSequenceRepository.findByBusinessIdentifierAndType(String.valueOf(accountId), RequestType.PERMIT_TRANSFER_B))
    		.thenReturn(Optional.of(requestSequence));
    	
    	//invoke
		final String result = generator.generate(params);
    	
    	assertThat(result).isEqualTo("AEMTB00001-" + (currentSequence + 1));

		final ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);  
    	verify(requestSequenceRepository, times(1)).save(requestSequenceCaptor.capture());
		final RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
    	assertThat(requestSequenceCaptured.getSequence()).isEqualTo(currentSequence + 1);
    }
}
