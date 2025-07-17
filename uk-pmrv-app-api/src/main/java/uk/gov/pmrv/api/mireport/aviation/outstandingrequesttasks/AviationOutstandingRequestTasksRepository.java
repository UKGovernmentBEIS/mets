package uk.gov.pmrv.api.mireport.aviation.outstandingrequesttasks;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.PostgresJpqlTemplates;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRegulatorRequestTasksMiReportParams;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRequestTask;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRequestTasksRepository;
import uk.gov.pmrv.api.account.aviation.domain.QAviationAccount;
import uk.gov.pmrv.api.account.domain.QLegalEntity;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestTask;

import java.util.List;

@Repository
public class AviationOutstandingRequestTasksRepository implements OutstandingRequestTasksRepository {

    @Override
    @Transactional(readOnly = true)
    public List<AviationOutstandingRequestTask> findOutstandingRequestTaskParams(EntityManager entityManager,
                                                                         OutstandingRegulatorRequestTasksMiReportParams params) {

        QRequest request = QRequest.request;
        QRequestTask requestTask = QRequestTask.requestTask;
        QAviationAccount account = QAviationAccount.aviationAccount;
        QLegalEntity legalEntity = QLegalEntity.legalEntity;

        JPAQuery<OutstandingRequestTask> query = new JPAQuery<>(entityManager, PostgresJpqlTemplates.DEFAULT);

        JPAQuery<AviationOutstandingRequestTask> jpaQuery = query.select(
                        Projections.constructor(
                                AviationOutstandingRequestTask.class,
                                account.emitterId,
                                account.accountType,
                                account.name,
                                legalEntity.name,
                                request.id,
                                request.type.stringValue(),
                                requestTask.type.stringValue(),
                                requestTask.assignee,
                                requestTask.dueDate,
                                requestTask.pauseDate,
                                account.crcoCode)
                )
                .from(request)
                .innerJoin(requestTask).on(request.id.eq(requestTask.request.id))
                .innerJoin(account).on(request.accountId.eq(account.id))
                .leftJoin(legalEntity).on(account.legalEntity.id.eq(legalEntity.id));

        BooleanExpression wherePredicate = requestTask.type.stringValue().in(params.getRequestTaskTypes());

        if (!params.getUserIds().isEmpty()) {
            wherePredicate = wherePredicate.and(requestTask.assignee.in(params.getUserIds()));
        }

        query.where(wherePredicate);
        query.orderBy(account.id.asc(), request.type.asc(), request.id.asc(), requestTask.type.asc());

        return jpaQuery.fetch();
    }
}
