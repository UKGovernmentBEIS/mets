package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service.EmpVariationUkEtsApprovedGenerateDocumentsService;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsRegulatorLedApprovedGenerateDocumentsService {
	
	private final EmpVariationUkEtsApprovedGenerateDocumentsService empVariationUkEtsApprovedGenerateDocumentsService;

	@Transactional
	public void generateDocuments(String requestId) {
		empVariationUkEtsApprovedGenerateDocumentsService.generateDocuments(requestId, true);
	}
}
