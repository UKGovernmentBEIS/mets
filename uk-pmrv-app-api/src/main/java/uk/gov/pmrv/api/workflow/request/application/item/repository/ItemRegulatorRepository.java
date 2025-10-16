package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.domain.QAccount;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemOrderBy;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.QRequestTaskVisit;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Map;
import java.util.Set;

@Repository
public class ItemRegulatorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ItemPage findItems(String userId,
                              ItemAssignmentType assignmentType,
                              Map<CompetentAuthorityEnum,
                              Set<RequestTaskType>> scopedCARequestTaskTypes,
                              PagingRequest paging,
                              ItemOrderBy orderBy,
                              RequestType requestType,
                              String accountSearchTerm) {
        QRequest request = QRequest.request;
        QRequestTask requestTask = QRequestTask.requestTask;
        QRequestTaskVisit requestTaskVisit = QRequestTaskVisit.requestTaskVisit;
        QAccount account = QAccount.account;

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
                .innerJoin(account)
                .on(account.id.eq(request.accountId))
                .leftJoin(requestTaskVisit)
                .on(requestTask.id.eq(requestTaskVisit.taskId).and(requestTaskVisit.userId.eq(userId)))
                .where(constructWherePredicate(userId,
                        assignmentType,
                        requestTask,
                        request,
                        account,
                		scopedCARequestTaskTypes,
                        requestType,
                        accountSearchTerm))
                .orderBy(orderBy.getOrderSpecifier())
                .offset((long) paging.getPageNumber() * paging.getPageSize())
                .limit(paging.getPageSize());

        return ItemPage.builder()
                .items(jpaQuery.fetch())
                .totalItems(jpaQuery.fetchCount())
                .build();
    }
    
	private Predicate constructWherePredicate(
            String userId,
            ItemAssignmentType assignmentType,
			QRequestTask requestTask,
            QRequest request,
            QAccount account,
			Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedCARequestTaskTypes,
            RequestType requestType,
            String accountSearchTerm) {

        BooleanExpression fullExpression;

        final BooleanExpression caRequestTaskScopeWhereClause = ItemRepoUtils
				.constructCARequestTaskScopeWhereClause(scopedCARequestTaskTypes, request, requestTask);


        fullExpression = caRequestTaskScopeWhereClause;

        if (requestType != null) {
              if(RequestType.SYSTEM_MESSAGE_NOTIFICATION.equals(requestType)) {
                fullExpression = request.type.eq(requestType);
            } else {
                fullExpression = fullExpression.and(request.type.eq(requestType));
            }
        } else {
            if (ItemAssignmentType.ME.equals(assignmentType)) {
                fullExpression = fullExpression.or(request.type.eq(RequestType.SYSTEM_MESSAGE_NOTIFICATION));
            }
        }

        if(accountSearchTerm != null) {

            BooleanExpression accountPredicate = Expressions.booleanTemplate(
                 "{0} ILIKE {1}",
                account.name,
                "%" + accountSearchTerm + "%"
            );

            try{
                accountPredicate = accountPredicate.or(account.id.eq(Long.valueOf(accountSearchTerm)));
            } catch (NumberFormatException ignored) {}

            fullExpression = fullExpression.and(accountPredicate);
        }

        return switch (assignmentType) {
            case ME -> requestTask.assignee.eq(userId).and(fullExpression);
            case OTHERS -> requestTask.assignee.ne(userId).and(fullExpression);
            case UNASSIGNED -> requestTask.assignee.isNull().and(fullExpression);
        };
	}
    	
}
