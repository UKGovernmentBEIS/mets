package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationApplicationRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsSubmitUploadAttachmentService {

	private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        EmpVariationApplicationRequestTaskPayload requestTaskPayload =
            (EmpVariationApplicationRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getEmpAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
    
}