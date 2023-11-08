package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class AviationAerContactUpdateService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void updateAerAviationApplicationSubmitTaskPayloadWithServiceContactDetails(Long accountId, ServiceContactDetails serviceContactDetails) {
        List<RequestTask> requestTasks = requestTaskService.findTasksByTypeInAndAccountId(
            Set.of(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT, RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT), 
            accountId
        );
        requestTasks.forEach(requestTask -> {
            AviationAerApplicationRequestTaskPayload payload = (AviationAerApplicationRequestTaskPayload) requestTask.getPayload();
            payload.setServiceContactDetails(serviceContactDetails);
            requestTaskService.updateRequestTaskPayload(requestTask, payload);
        });
    }
}
