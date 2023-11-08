package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

@Service
@RequiredArgsConstructor
public class RequestTaskTimeManagementService {

    private final RequestTaskRepository requestTaskRepository;

    public List<RequestTask> setDueDateToTasks(String requestId, RequestExpirationType requestExpirationType, LocalDate dueDate) {
    	List<RequestTask> requestTasks = findAssociatedTasks(requestId, requestExpirationType);
    	requestTasks.forEach(requestTask -> requestTask.setDueDate(dueDate));
        return requestTasks;
    }
    
    public void pauseTasks(String requestId, RequestExpirationType requestExpirationType) {
    	List<RequestTask> requestTasks = findAssociatedTasks(requestId, requestExpirationType);
    	requestTasks.forEach(requestTask -> requestTask.setPauseDate(LocalDate.now()));
    }
    
    public void unpauseTasksAndUpdateDueDate(String requestId, RequestExpirationType requestExpirationType, LocalDate dueDate) {
    	List<RequestTask> requestTasks = findAssociatedTasks(requestId, requestExpirationType);
    	requestTasks.forEach(requestTask -> {
            requestTask.setPauseDate(null);
            requestTask.setDueDate(dueDate);
        });
    }
    
    private List<RequestTask> findAssociatedTasks(String requestId, RequestExpirationType requestExpirationType) {
    	return requestTaskRepository.findByRequestId(requestId).stream()
    				.filter(task -> task.getType().isExpirable() && 
    									task.getType().getExpirationKey() == requestExpirationType)
    				.toList();
    }

}
