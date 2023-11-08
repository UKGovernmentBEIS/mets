package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationGrantedGenerateDocumentsService;

@ExtendWith(MockitoExtension.class)
class PermitVariationOperatorLedGrantedGenerateDocumentsServiceTest {

	@InjectMocks
    private PermitVariationOperatorLedGrantedGenerateDocumentsService cut;
	
	@Mock
	private PermitVariationGrantedGenerateDocumentsService permitVariationGrantedGenerateDocumentsService;
	
	@Test
	void generateDocuments() {
		String requestId = "requestId";
		cut.generateDocuments(requestId);
		
		verify(permitVariationGrantedGenerateDocumentsService, times(1)).generateDocuments(requestId, false);
	}
}
