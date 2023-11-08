package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;

import org.springframework.stereotype.Repository;

import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class ItemByRequestVerifierRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ItemPage findItemsByRequestId(Map<Long, Set<RequestTaskType>> scopedAccountRequestTaskTypes, String requestId) {
        QRequest request = QRequest.request;
        QRequestTask requestTask = QRequestTask.requestTask;

        JPAQuery<Item> query = new JPAQuery<>(entityManager);

        final BooleanExpression accountRequestTaskScopeWhereClause = ItemRepoUtils.constructAccountRequestTaskScopeWhereClause(
        		scopedAccountRequestTaskTypes, request, requestTask);

        JPAQuery<Item> jpaQuery = query.select(
                Projections.constructor(Item.class,
                        requestTask.startDate,
                        request.id, request.type, request.accountId,
                        requestTask.id, requestTask.type, requestTask.assignee,
                        requestTask.dueDate, requestTask.pauseDate, Expressions.FALSE))
                .from(request)
                .innerJoin(requestTask)
                .on(request.id.eq(requestTask.request.id))
                .where(request.id.eq(requestId)
                        .and(accountRequestTaskScopeWhereClause)
                ).orderBy(requestTask.startDate.desc());

        List<Item> items = jpaQuery.fetch();
        return ItemPage.builder()
                .items(items)
                .totalItems((long) items.size())
                .build();
    }
}
