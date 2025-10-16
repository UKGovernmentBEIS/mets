package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.corsia.EmpCorsiaPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service.EmpVariationCorsiaPreviewEmpDocumentService;

import java.util.List;

@Service
public class EmpVariationCorsiaPeerReviewPreviewEmpDocumentService extends EmpVariationCorsiaPreviewEmpDocumentService {

    public EmpVariationCorsiaPeerReviewPreviewEmpDocumentService(RequestTaskService requestTaskService,
                                                                 EmpCorsiaPreviewCreateEmpDocumentService
                                                                     empCorsiaPreviewCreateEmpDocumentService,
                                                                 EmissionsMonitoringPlanQueryService
                                                                     emissionsMonitoringPlanQueryService) {
        super(requestTaskService, empCorsiaPreviewCreateEmpDocumentService, emissionsMonitoringPlanQueryService);
    }

    @Override
    public List<RequestTaskType> getTypes() {
        return List.of(RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW);
    }

}
