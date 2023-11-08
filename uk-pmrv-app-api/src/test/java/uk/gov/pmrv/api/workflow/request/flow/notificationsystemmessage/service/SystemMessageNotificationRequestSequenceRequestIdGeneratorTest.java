package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemMessageNotificationRequestSequenceRequestIdGeneratorTest {

	@InjectMocks
    private SystemMessageNotificationRequestSequenceRequestIdGenerator cut;
    
    @Mock
    private RequestSequenceRepository requestSequenceRepository;

	@Test
	void getTypes() {
		assertThat(cut.getTypes()).containsExactly(RequestType.SYSTEM_MESSAGE_NOTIFICATION);
	}
    
    @Test
    void getPrefix() {
    	assertThat(cut.getPrefix()).isNull();
    }
    
    @Test
    void generate() {
    	long currentSequence = 3;
    	RequestParams params = RequestParams.builder()
    			.type(RequestType.SYSTEM_MESSAGE_NOTIFICATION)
    			.build();
    	
    	RequestSequence requestSequence = RequestSequence.builder()
    			.id(2L)
    			.sequence(currentSequence)
    			.type(RequestType.SYSTEM_MESSAGE_NOTIFICATION)
    			.build();
    	
    	when(requestSequenceRepository.findByType(RequestType.SYSTEM_MESSAGE_NOTIFICATION))
    		.thenReturn(Optional.of(requestSequence));
    	
    	//invoke
    	String result = cut.generate(params);
    	
    	assertThat(result).isEqualTo(String.valueOf(currentSequence + 1));
    	verify(requestSequenceRepository, times(1)).findByType(RequestType.SYSTEM_MESSAGE_NOTIFICATION);
    	ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);  
    	verify(requestSequenceRepository, times(1)).save(requestSequenceCaptor.capture());
    	RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
    	assertThat(requestSequenceCaptured.getSequence()).isEqualTo(currentSequence + 1);
    }
}
