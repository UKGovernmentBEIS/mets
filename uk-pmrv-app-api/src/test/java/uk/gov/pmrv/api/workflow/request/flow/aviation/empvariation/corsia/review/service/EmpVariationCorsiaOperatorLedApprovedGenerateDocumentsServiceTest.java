package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service.EmpVariationCorsiaApprovedGenerateDocumentsService;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaOperatorLedApprovedGenerateDocumentsServiceTest {

	@InjectMocks
    private EmpVariationCorsiaOperatorLedApprovedGenerateDocumentsService service;
	
	@Mock
	private EmpVariationCorsiaApprovedGenerateDocumentsService empVariationCorsiaApprovedGenerateDocumentsService;
	
	@Test
	void generateDocuments() {
		String requestId = "requestId";
		service.generateDocuments(requestId);
		
		verify(empVariationCorsiaApprovedGenerateDocumentsService, times(1)).generateDocuments(requestId, false);
	}
}
