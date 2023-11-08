package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreRequestIdGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestIdGeneratorResolverTest {

	@InjectMocks
    private RequestIdGeneratorResolver cut;
	
	@Mock
    private DreRequestIdGenerator dreRequestIdGenerator;

    @Spy
    private ArrayList<RequestIdGenerator> requestIdGenerators;

    @BeforeEach
    void setUp() {
    	requestIdGenerators.add(dreRequestIdGenerator);
    }
    
    @Test
    void get() {
    	when(dreRequestIdGenerator.getTypes()).thenReturn(List.of(RequestType.DRE));
    	
    	assertThat(cut.get(RequestType.DRE)).isEqualTo(dreRequestIdGenerator);
    	
    	verify(dreRequestIdGenerator, times(1)).getTypes();
    }
    
    @Test
    void get_not_found_throws_exception() {
        when(dreRequestIdGenerator.getTypes()).thenReturn(List.of(RequestType.DRE));
    	
    	IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cut.get(RequestType.AER));
    	
    	assertThat(ex.getMessage()).isEqualTo("Could not resolve request id generator for request type: AER");
    	verify(dreRequestIdGenerator, times(1)).getTypes();
    }
}
