package uk.gov.pmrv.api.workflow.request.flow.aviation.common.handler.emp;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpPreviewDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;

@Service
public class EmpPreviewHandler extends PreviewDocumentAbstractHandler {

    private final List<EmpPreviewDocumentService> services;
    
    public EmpPreviewHandler(final RequestTaskService requestTaskService,
                             final List<EmpPreviewDocumentService> services) {
        super(requestTaskService);
        this.services = services;
    }

    @Override
    protected FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification) {
        
        final RequestTaskType taskType = requestTaskService.findTaskById(taskId).getType();
        final EmpPreviewDocumentService service =
            services.stream().filter(s -> s.getTypes().contains(taskType)).findFirst().orElseThrow(
                () -> new BusinessException(MetsErrorCode.INVALID_DOCUMENT_TEMPLATE_FOR_REQUEST_TASK, taskType)
            );
        return service.create(taskId, decisionNotification);
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(
            DocumentTemplateType.EMP_UKETS,
            DocumentTemplateType.EMP_CORSIA
        );
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(
            RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW,
            RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW,
            RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW,
            RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT,
            RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW,
            RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW,
            RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW,
            RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT
        );
    }
}
