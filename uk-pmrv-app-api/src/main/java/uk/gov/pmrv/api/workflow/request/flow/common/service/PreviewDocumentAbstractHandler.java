package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@RequiredArgsConstructor
public abstract class PreviewDocumentAbstractHandler implements PreviewDocumentHandler {

    protected final RequestTaskService requestTaskService;

    @Transactional(readOnly = true)
    public FileDTO previewDocument(final Long taskId, final DecisionNotification decisionNotification) {

        this.validateTaskType(taskId);
        return this.generateDocument(taskId, decisionNotification);
    }

    private void validateTaskType(final Long taskId) {

        final RequestTaskType taskType = requestTaskService.findTaskById(taskId).getType();
        final boolean valid = this.getTaskTypes().contains(taskType);
        if (!valid) {
            throw new BusinessException(MetsErrorCode.INVALID_DOCUMENT_TEMPLATE_FOR_REQUEST_TASK, taskType);
        }
    }

    protected abstract List<RequestTaskType> getTaskTypes();
    
    protected abstract FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification);
}
