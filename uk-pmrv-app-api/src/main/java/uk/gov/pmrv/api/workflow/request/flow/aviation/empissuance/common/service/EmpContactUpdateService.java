package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceApplicationRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpContactUpdateService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void updateEmpApplicationSubmitTaskPayloadWithServiceContactDetails(Long accountId, ServiceContactDetails serviceContactDetails) {
        List<RequestTask> requestTasks = requestTaskService.findTasksByTypeInAndAccountId(
            Set.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT, RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT), 
            accountId
        );
        requestTasks.forEach(requestTask -> {
            EmpIssuanceApplicationRequestTaskPayload payload = (EmpIssuanceApplicationRequestTaskPayload) requestTask.getPayload();
            payload.setServiceContactDetails(serviceContactDetails);
            requestTaskService.updateRequestTaskPayload(requestTask, payload);
        });
    }
}
