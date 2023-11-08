package uk.gov.pmrv.api.mireport.installation.executedactions;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.account.installation.domain.QInstallationAccount;
import uk.gov.pmrv.api.account.domain.QLegalEntity;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestAction;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsRepository;
import uk.gov.pmrv.api.permit.domain.QPermitEntity;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestAction;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public class InstallationExecutedRequestActionsRepository implements ExecutedRequestActionsRepository {

    public List<ExecutedRequestAction> findExecutedRequestActions(EntityManager entityManager, ExecutedRequestActionsMiReportParams reportParams) {
        QRequest request = QRequest.request;
        QRequestAction requestAction = QRequestAction.requestAction;
        QInstallationAccount account = QInstallationAccount.installationAccount;
        QLegalEntity legalEntity = QLegalEntity.legalEntity;
        QPermitEntity permit = QPermitEntity.permitEntity;

        JPAQuery<ExecutedRequestAction> query = new JPAQuery<>(entityManager);

        BooleanBuilder isCreationDateBeforeToDate = new BooleanBuilder();
        if(reportParams.getToDate() != null){
            isCreationDateBeforeToDate.and(requestAction.creationDate.before(LocalDateTime.of(reportParams.getToDate(), LocalTime.MIDNIGHT)));
        }

        JPAQuery<ExecutedRequestAction> jpaQuery = query.select(
            Projections.constructor(ExecutedRequestAction.class,
                account.emitterId, account.accountType, account.name, account.status.stringValue(),
                legalEntity.name, permit.id,
                request.id, request.type, request.status,
                requestAction.type, requestAction.submitter, requestAction.creationDate))
            .from(request)
            .innerJoin(requestAction).on(request.id.eq(requestAction.request.id))
            .innerJoin(account).on(request.accountId.eq(account.id))
            .innerJoin(legalEntity).on(account.legalEntity.id.eq(legalEntity.id))
            .leftJoin(permit).on(account.id.eq(permit.accountId))
            .where(requestAction.creationDate.goe(LocalDateTime.of(reportParams.getFromDate(), LocalTime.MIDNIGHT))
                    .and(isCreationDateBeforeToDate))
            .orderBy(account.id.asc(),request.type.asc(), request.id.asc(), requestAction.creationDate.asc());

        return jpaQuery.fetch();
    }
}
