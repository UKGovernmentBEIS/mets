package uk.gov.pmrv.api.workflow.request.core.repository;

import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.List;

import static uk.gov.pmrv.api.workflow.request.core.domain.RequestTask.NAMED_ENTITY_GRAPH_REQUEST_TASK_REQUEST;

@Repository
public interface RequestTaskRepository extends JpaRepository<RequestTask, Long> {

    @Transactional(readOnly = true)
    RequestTask findByProcessTaskId(String processTaskId);

    @Transactional(readOnly = true)
    List<RequestTask> findByRequestTypeAndAssignee(
            RequestType requestType, String assignee);
    
    @Transactional(readOnly = true)
    @EntityGraph(value = NAMED_ENTITY_GRAPH_REQUEST_TASK_REQUEST, type = EntityGraph.EntityGraphType.FETCH)
    List<RequestTask> findByAssigneeAndRequestStatus(
            String assignee, RequestStatus requestStatus);

    @Transactional(readOnly = true)
    List<RequestTask> findByRequestTypeAndAssigneeAndRequestAccountId(
        RequestType requestType, String assignee, Long accountId);

    @Transactional(readOnly = true)
    List<RequestTask> findByAssigneeAndRequestAccountIdAndRequestStatus(
        String assignee, Long accountId, RequestStatus requestStatus);

    @Transactional(readOnly = true)
    List<RequestTask> findByRequestId(String requestId);

    @Transactional(readOnly = true)
    List<RequestTask> findByTypeInAndRequestAccountId(Set<RequestTaskType> type, Long accountId);

    @Transactional(readOnly = true)
    @Query("select distinct req.accountId "
        + "from Request req "
        + "join RequestTask task "
        + "on req.id = task.request.id "
        + "where task.assignee = :userId "
        + "and task.type in (:taskTypes) "
        + "and req.verificationBodyId = :vbId")
    List<Long> findAccountIdsByTaskAssigneeAndTaskTypeInAndVerificationBody(String userId,
                                                                            Set<RequestTaskType> taskTypes, 
                                                                            Long vbId);
    
}
