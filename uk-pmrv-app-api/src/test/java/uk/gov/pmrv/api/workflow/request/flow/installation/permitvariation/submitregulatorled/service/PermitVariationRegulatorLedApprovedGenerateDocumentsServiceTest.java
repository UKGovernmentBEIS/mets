package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationGrantedGenerateDocumentsService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationRegulatorLedApprovedGenerateDocumentsService;

@ExtendWith(MockitoExtension.class)
class PermitVariationRegulatorLedApprovedGenerateDocumentsServiceTest {
	
	@InjectMocks
    private PermitVariationRegulatorLedApprovedGenerateDocumentsService cut;
	
	@Mock
	private PermitVariationGrantedGenerateDocumentsService permitVariationGrantedGenerateDocumentsService;
	
	@Test
	void generateDocuments() {
		String requestId = "requestId";
		cut.generateDocuments(requestId);
		
		verify(permitVariationGrantedGenerateDocumentsService, times(1)).generateDocuments(requestId, true);
	}
}
