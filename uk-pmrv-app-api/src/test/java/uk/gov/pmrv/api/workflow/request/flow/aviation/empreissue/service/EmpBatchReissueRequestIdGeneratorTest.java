package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

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

import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class EmpBatchReissueRequestIdGeneratorTest {

	@InjectMocks
    private EmpBatchReissueRequestIdGenerator cut;
    
    @Mock
    private RequestSequenceRepository requestSequenceRepository;

	@Test
	void getTypes() {
		assertThat(cut.getTypes()).containsExactly(RequestType.EMP_BATCH_REISSUE);
	}
    
    @Test
    void getPrefix() {
    	assertThat(cut.getPrefix()).isEqualTo("BRA");
    }
    
    @Test
    void generate() {
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	long currentSequence = 153;
    	RequestParams params = RequestParams.builder()
    			.competentAuthority(competentAuthority)
    			.type(RequestType.EMP_BATCH_REISSUE)
    			.build();
    	
    	RequestSequence requestSequence = RequestSequence.builder()
    			.id(2L)
    			.businessIdentifier(competentAuthority.name())
    			.sequence(currentSequence)
    			.type(RequestType.EMP_BATCH_REISSUE)
    			.build();
    	
    	when(requestSequenceRepository.findByBusinessIdentifierAndType(competentAuthority.name(), RequestType.EMP_BATCH_REISSUE))
    		.thenReturn(Optional.of(requestSequence));
    	
    	//invoke
    	String result = cut.generate(params);
    	
    	assertThat(result).isEqualTo("BRA0154-E");
    	verify(requestSequenceRepository, times(1)).findByBusinessIdentifierAndType(competentAuthority.name(), RequestType.EMP_BATCH_REISSUE);
    	ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);  
    	verify(requestSequenceRepository, times(1)).save(requestSequenceCaptor.capture());
    	RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
    	assertThat(requestSequenceCaptured.getSequence()).isEqualTo(currentSequence + 1);
    }
    
    @Test
    void generate_new() {
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	RequestParams params = RequestParams.builder()
    			.competentAuthority(competentAuthority)
    			.type(RequestType.EMP_BATCH_REISSUE)
    			.build();
    	
    	when(requestSequenceRepository.findByBusinessIdentifierAndType(competentAuthority.name(), RequestType.EMP_BATCH_REISSUE))
    		.thenReturn(Optional.empty());
    	
    	//invoke
    	String result = cut.generate(params);
    	
    	assertThat(result).isEqualTo("BRA0001-E");
    	verify(requestSequenceRepository, times(1)).findByBusinessIdentifierAndType(competentAuthority.name(), RequestType.EMP_BATCH_REISSUE);
    	ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);  
    	verify(requestSequenceRepository, times(1)).save(requestSequenceCaptor.capture());
    	RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
    	assertThat(requestSequenceCaptured.getSequence()).isEqualTo(1);
    }
}
