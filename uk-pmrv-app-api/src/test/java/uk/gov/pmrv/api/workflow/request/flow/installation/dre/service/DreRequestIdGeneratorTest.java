package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestMetadata;

import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DreRequestIdGeneratorTest {

	@InjectMocks
    private DreRequestIdGenerator cut;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Test
    void generate() {
    	long currentSequence = 1;
    	Long accountId = 1L;
    	Year year = Year.of(2023);
    	RequestParams params = RequestParams.builder()
    			.accountId(accountId)
    			.type(RequestType.DRE)
    			.requestMetadata(DreRequestMetadata.builder().year(year).build())
    			.build();
    	
    	String businessIdentifierKey = accountId + "-" + year.getValue();
    	
    	RequestSequence requestSequence = RequestSequence.builder()
    			.id(2L)
    			.businessIdentifier(businessIdentifierKey)
    			.sequence(currentSequence)
    			.type(RequestType.DRE)
    			.build();
    	
    	when(requestSequenceRepository.findByBusinessIdentifierAndType(businessIdentifierKey, RequestType.DRE))
    		.thenReturn(Optional.of(requestSequence));
    	
    	final String result = cut.generate(params);
    	
    	assertThat(result).isEqualTo("DRE" + "00001" + "-" + year + "-" + (currentSequence + 1));
    	final ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);  
    	verify(requestSequenceRepository, times(1)).save(requestSequenceCaptor.capture());
		final RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
    	assertThat(requestSequenceCaptured.getSequence()).isEqualTo(currentSequence + 1);
    }

	@Test
	void getTypes() {
		assertThat(cut.getTypes()).containsExactly(RequestType.DRE);
	}
    
    @Test
    void getPrefix() {
        assertThat(cut.getPrefix()).isEqualTo("DRE");
    }
}
