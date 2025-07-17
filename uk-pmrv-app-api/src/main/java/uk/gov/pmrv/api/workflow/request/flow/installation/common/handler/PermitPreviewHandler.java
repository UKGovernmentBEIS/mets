package uk.gov.pmrv.api.workflow.request.flow.installation.common.handler;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewDocumentService;

import java.util.List;

@Service
public class PermitPreviewHandler extends PreviewDocumentAbstractHandler {

    private final List<PermitPreviewDocumentService> services;
    
    public PermitPreviewHandler(final RequestTaskService requestTaskService,
                                final List<PermitPreviewDocumentService> services) {
        super(requestTaskService);
        this.services = services;
    }

    @Override
    protected FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification) {
        
        final RequestTaskType taskType = requestTaskService.findTaskById(taskId).getType();
        final PermitPreviewDocumentService service =
            services.stream().filter(s -> s.getType().equals(taskType)).findFirst().orElseThrow(
                () -> new BusinessException(MetsErrorCode.INVALID_DOCUMENT_TEMPLATE_FOR_REQUEST_TASK, taskType)
            );
        return service.create(taskId, decisionNotification);
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.PERMIT);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW,
                        RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW,
                       RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW,
                       RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT,
                        RequestTaskType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW,
                        RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_REVIEW);
    }
}
