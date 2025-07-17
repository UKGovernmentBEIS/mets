package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationGrantedGenerateDocumentsService;

@Service
@RequiredArgsConstructor
public class PermitVariationRegulatorLedApprovedGenerateDocumentsService {
	
	private final PermitVariationGrantedGenerateDocumentsService permitVariationGrantedGenerateDocumentsService;
	
	@Transactional
	public void generateDocuments(String requestId) {
		permitVariationGrantedGenerateDocumentsService.generateDocuments(requestId, true);
	}
}
