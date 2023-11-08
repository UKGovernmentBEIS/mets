package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceApplicationRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpCorsiaSectionUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        EmpIssuanceApplicationRequestTaskPayload requestTaskPayload =
            (EmpIssuanceApplicationRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getEmpAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
