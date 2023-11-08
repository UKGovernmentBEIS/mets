package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service.EmpVariationCorsiaApprovedGenerateDocumentsService;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaRegulatorLedApprovedGenerateDocumentsService {

    private final EmpVariationCorsiaApprovedGenerateDocumentsService documentsService;

    @Transactional
    public void generateDocuments(String requestId) {
        documentsService.generateDocuments(requestId, true);
    }
}
