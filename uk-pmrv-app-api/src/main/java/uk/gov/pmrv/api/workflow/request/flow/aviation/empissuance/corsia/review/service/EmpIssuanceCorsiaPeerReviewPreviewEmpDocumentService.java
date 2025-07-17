package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service;

import java.util.List;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.corsia.EmpCorsiaPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.service.EmpIssuanceCorsiaPreviewEmpDocumentService;

@Service
public class EmpIssuanceCorsiaPeerReviewPreviewEmpDocumentService extends EmpIssuanceCorsiaPreviewEmpDocumentService {

    public EmpIssuanceCorsiaPeerReviewPreviewEmpDocumentService(RequestTaskService requestTaskService,
                                                                EmpCorsiaPreviewCreateEmpDocumentService
                                                                        empCorsiaPreviewCreateEmpDocumentService) {
        super(requestTaskService, empCorsiaPreviewCreateEmpDocumentService);
    }

    @Override
    public List<RequestTaskType> getTypes() {
        return List.of(RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW);
    }
}
