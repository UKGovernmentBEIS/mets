package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service.EmpVariationCorsiaApprovedGenerateDocumentsService;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaOperatorLedApprovedGenerateDocumentsService {

	private final EmpVariationCorsiaApprovedGenerateDocumentsService empVariationCorsiaApprovedGenerateDocumentsService;
	
	@Transactional
	public void generateDocuments(String requestId) {
		empVariationCorsiaApprovedGenerateDocumentsService.generateDocuments(requestId, false);
	}
}
