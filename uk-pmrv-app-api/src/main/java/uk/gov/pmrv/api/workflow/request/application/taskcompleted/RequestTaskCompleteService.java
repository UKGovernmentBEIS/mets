package uk.gov.pmrv.api.workflow.request.application.taskcompleted;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskHistory;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestTaskCompleteService {

    private final RequestTaskRepository requestTaskRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void complete(String processTaskId) {
        RequestTask requestTask = requestTaskRepository.findByProcessTaskId(processTaskId);

        eventPublisher.publishEvent(RequestTaskCompletedEvent.builder()
                .requestTaskId(requestTask.getId()).build());

        RequestTaskHistory requestTaskHistory =
                RequestTaskHistory.builder()
                        .id(requestTask.getId())
                        .processTaskId(processTaskId)
                        .type(requestTask.getType())
                        .assignee(requestTask.getAssignee())
                        .dueDate(requestTask.getDueDate())
                        .startDate(requestTask.getStartDate())
                        .pauseDate(requestTask.getPauseDate())
                        .payload(requestTask.getPayload())
                        .endDate(LocalDateTime.now())
                        .build();

        requestTask.getRequest().addRequestTaskHistory(requestTaskHistory);
        requestTaskRepository.delete(requestTask);
    }
}
