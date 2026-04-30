package uk.gov.pmrv.api.workflow.request.application.taskdeleted;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestTaskDeleteService {

    private final RequestTaskRepository requestTaskRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void delete(final String processTaskId) {

        //Optional here is checked as this service can also be called for a task that has already been completed and deleted by RequestTaskCompleteService
        final Optional<RequestTask> requestTask = Optional.ofNullable(requestTaskRepository.findByProcessTaskId(processTaskId));
        requestTask.ifPresent(task -> {
            eventPublisher.publishEvent(RequestTaskDeletedEvent.builder().requestTaskId(task.getId()).build());
            requestTaskRepository.delete(task);
        });

    }

}
