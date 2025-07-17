package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.QRequestTaskVisit;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Map;
import java.util.Set;

@Repository
public class ItemOperatorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ItemPage findItems(String userId, ItemAssignmentType assignmentType, Map<Long, Set<RequestTaskType>> scopedAccountRequestTaskTypes, PagingRequest paging) {
        QRequest request = QRequest.request;
        QRequestTask requestTask = QRequestTask.requestTask;
        QRequestTaskVisit requestTaskVisit = QRequestTaskVisit.requestTaskVisit;

        JPAQuery<Item> query = new JPAQuery<>(entityManager);
        
        JPAQuery<Item> jpaQuery = query.select(
                Projections.constructor(Item.class,
                        requestTask.startDate,
                        request.id, request.type, request.accountId,
                        requestTask.id, requestTask.type, requestTask.assignee,
                        requestTask.dueDate, requestTask.pauseDate, requestTaskVisit.isNull()))
                .from(request)
                .innerJoin(requestTask)
                .on(request.id.eq(requestTask.request.id))
                .leftJoin(requestTaskVisit)
                .on(requestTask.id.eq(requestTaskVisit.taskId).and(requestTaskVisit.userId.eq(userId)))
				.where(constructWherePredicate(userId, assignmentType, requestTask, request,
						scopedAccountRequestTaskTypes))
                .orderBy(requestTask.startDate.desc())
                .offset(paging.getPageNumber() * paging.getPageSize())
                .limit(paging.getPageSize());

        return ItemPage.builder()
                .items(jpaQuery.fetch())
                .totalItems(jpaQuery.fetchCount())
                .build();
    }
    
    private Predicate constructWherePredicate(String userId, ItemAssignmentType assignmentType,
			QRequestTask requestTask, QRequest request,
			Map<Long, Set<RequestTaskType>> scopedAccountRequestTaskTypes) {
    	final BooleanExpression accountRequestTaskScopeWhereClause = ItemRepoUtils.constructAccountRequestTaskScopeWhereClause(
				scopedAccountRequestTaskTypes, request, requestTask);

        return switch (assignmentType) {
            case ME -> requestTask.assignee.eq(userId)
                    .and(accountRequestTaskScopeWhereClause
                            .or(request.type.eq(RequestType.SYSTEM_MESSAGE_NOTIFICATION)));
            case OTHERS -> requestTask.assignee.ne(userId).and(accountRequestTaskScopeWhereClause);
            case UNASSIGNED -> requestTask.assignee.isNull().and(accountRequestTaskScopeWhereClause);
        };
	}
    
}
