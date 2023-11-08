package uk.gov.pmrv.api.mireport.aviation.executedactions;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.account.aviation.domain.QAviationAccount;
import uk.gov.pmrv.api.account.domain.QLegalEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.QEmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestAction;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestAction;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public class AviationExecutedRequestActionsRepository implements ExecutedRequestActionsRepository {

    public List<ExecutedRequestAction> findExecutedRequestActions(EntityManager entityManager, ExecutedRequestActionsMiReportParams reportParams) {
        QRequest request = QRequest.request;
        QRequestAction requestAction = QRequestAction.requestAction;
        QAviationAccount account = QAviationAccount.aviationAccount;
        QLegalEntity legalEntity = QLegalEntity.legalEntity;
        QEmissionsMonitoringPlanEntity emissionsMonitoringPlan = QEmissionsMonitoringPlanEntity.emissionsMonitoringPlanEntity;

        JPAQuery<ExecutedRequestAction> query = new JPAQuery<>(entityManager);

        BooleanBuilder isCreationDateBeforeToDate = new BooleanBuilder();
        if(reportParams.getToDate() != null){
            isCreationDateBeforeToDate.and(requestAction.creationDate.before(LocalDateTime.of(reportParams.getToDate(), LocalTime.MIDNIGHT)));
        }

        JPAQuery<ExecutedRequestAction> jpaQuery = query.select(
                Projections.constructor(ExecutedRequestAction.class,
                    account.emitterId, account.accountType, account.name, account.status.stringValue(),
                    legalEntity.name, emissionsMonitoringPlan.id,
                    request.id, request.type, request.status,
                    requestAction.type, requestAction.submitter, requestAction.creationDate))
            .from(request)
            .innerJoin(requestAction).on(request.id.eq(requestAction.request.id))
            .innerJoin(account).on(request.accountId.eq(account.id))
            .leftJoin(legalEntity).on(account.legalEntity.id.eq(legalEntity.id))
            .leftJoin(emissionsMonitoringPlan).on(account.id.eq(emissionsMonitoringPlan.accountId))
            .where(requestAction.creationDate.goe(LocalDateTime.of(reportParams.getFromDate(), LocalTime.MIDNIGHT))
                .and(isCreationDateBeforeToDate))
            .orderBy(account.id.asc(),request.type.asc(), request.id.asc(), requestAction.creationDate.asc());

        return jpaQuery.fetch();
    }
}
