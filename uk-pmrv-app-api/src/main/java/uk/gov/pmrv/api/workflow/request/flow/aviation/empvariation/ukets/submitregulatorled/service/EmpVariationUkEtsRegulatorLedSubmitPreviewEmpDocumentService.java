package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service;

import java.util.List;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.ukets.EmpUkEtsPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service.EmpVariationUkEtsPreviewEmpDocumentService;

@Service
public class EmpVariationUkEtsRegulatorLedSubmitPreviewEmpDocumentService
        extends EmpVariationUkEtsPreviewEmpDocumentService {


    public EmpVariationUkEtsRegulatorLedSubmitPreviewEmpDocumentService(RequestTaskService requestTaskService,
                                                                    EmpUkEtsPreviewCreateEmpDocumentService
                                                                            empUkEtsPreviewCreateEmpDocumentService,
                                                                        EmissionsMonitoringPlanQueryService
                                                                                emissionsMonitoringPlanQueryService) {
        super(requestTaskService, empUkEtsPreviewCreateEmpDocumentService, emissionsMonitoringPlanQueryService);
    }

    @Override
    public List<RequestTaskType> getTypes() {
        return List.of(RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT);
    }
}
