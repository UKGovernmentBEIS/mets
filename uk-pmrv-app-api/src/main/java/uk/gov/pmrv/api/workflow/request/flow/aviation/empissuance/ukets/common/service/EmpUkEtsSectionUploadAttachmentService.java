package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceApplicationRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmpUkEtsSectionUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        EmpIssuanceApplicationRequestTaskPayload requestTaskPayload =
            (EmpIssuanceApplicationRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getEmpAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
