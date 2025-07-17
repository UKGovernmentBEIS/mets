package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service;

import java.util.List;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.corsia.EmpCorsiaPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service.EmpVariationCorsiaPreviewEmpDocumentService;

@Service
public class EmpVariationCorsiaRegulatorLedSubmitPreviewEmpDocumentService
        extends EmpVariationCorsiaPreviewEmpDocumentService {

    public EmpVariationCorsiaRegulatorLedSubmitPreviewEmpDocumentService(RequestTaskService requestTaskService,
                                                                         EmpCorsiaPreviewCreateEmpDocumentService
                                                                                 empCorsiaPreviewCreateEmpDocumentService,
                                                                         EmissionsMonitoringPlanQueryService
                                                                                 emissionsMonitoringPlanQueryService) {
        super(requestTaskService, empCorsiaPreviewCreateEmpDocumentService, emissionsMonitoringPlanQueryService);
    }

    @Override
    public List<RequestTaskType> getTypes() {
        return List.of(RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT);
    }
}
