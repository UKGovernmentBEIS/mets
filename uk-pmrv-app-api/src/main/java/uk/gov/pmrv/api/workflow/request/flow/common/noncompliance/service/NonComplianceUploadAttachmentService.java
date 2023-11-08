package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestTaskAttachable;

@Service
@RequiredArgsConstructor
public class NonComplianceUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(final Long requestTaskId, 
                                 final String attachmentUuid, 
                                 final String filename) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NonComplianceRequestTaskAttachable requestTaskPayload = 
            (NonComplianceRequestTaskAttachable) requestTask.getPayload();
        requestTaskPayload.getNonComplianceAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
