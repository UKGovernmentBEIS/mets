package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service.EmpVariationUkEtsApprovedGenerateDocumentsService;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsOperatorLedApprovedGenerateDocumentsServiceTest {

	@InjectMocks
    private EmpVariationUkEtsOperatorLedApprovedGenerateDocumentsService service;
	
	@Mock
	private EmpVariationUkEtsApprovedGenerateDocumentsService empVariationUkEtsApprovedGenerateDocumentsService;
	
	@Test
	void generateDocuments() {
		String requestId = "requestId";
		service.generateDocuments(requestId);
		
		verify(empVariationUkEtsApprovedGenerateDocumentsService, times(1)).generateDocuments(requestId, false);
	}
}
