package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.ukets.EmpUkEtsPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service.EmpIssuanceUkEtsPreviewEmpDocumentService;


@Service
public class EmpIssuanceUkEtsPeerReviewPreviewEmpDocumentService extends EmpIssuanceUkEtsPreviewEmpDocumentService {
    public EmpIssuanceUkEtsPeerReviewPreviewEmpDocumentService(RequestTaskService requestTaskService,
                                                               EmpUkEtsPreviewCreateEmpDocumentService
                                                                       empUkEtsPreviewCreateEmpDocumentService) {
        super(requestTaskService, empUkEtsPreviewCreateEmpDocumentService);
    }

    @Override
    public List<RequestTaskType> getTypes() {
        return List.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW);
    }
}
