package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.experimental.UtilityClass;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@UtilityClass
class ItemRepoUtils {
	
	public BooleanExpression constructAccountRequestTaskScopeWhereClause(Map<Long, Set<RequestTaskType>> scopedAccountRequestTaskTypes,
			QRequest request, QRequestTask requestTask) {
		List<BooleanExpression> orExpressions = new ArrayList<>();
		scopedAccountRequestTaskTypes.forEach((accountId, types) -> orExpressions.add(request.accountId.eq(accountId).and(requestTask.type.in(types))));
        
		return Expressions.booleanTemplate(
				ItemRepoUtils.constructMultipleOrWhereTemplate(orExpressions.size()), orExpressions);
	}
	
	public BooleanExpression constructCARequestTaskScopeWhereClause(Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedCARequestTaskTypes,
			QRequest request, QRequestTask requestTask) {
		List<BooleanExpression> orExpressions = new ArrayList<>();
		scopedCARequestTaskTypes.forEach((ca, types) -> orExpressions.add(request.competentAuthority.eq(ca).and(requestTask.type.in(types))));
        
		return Expressions.booleanTemplate(
				ItemRepoUtils.constructMultipleOrWhereTemplate(orExpressions.size()), orExpressions);
	}

	private String constructMultipleOrWhereTemplate(int scopedRequestTaskTypesSize) {
		StringBuilder templateBuilder;
		if(scopedRequestTaskTypesSize == 0) {
        	templateBuilder = new StringBuilder("(1 = -1)");
        } else {
        	templateBuilder = new StringBuilder("(({0})");
            for (int i = 1; i < scopedRequestTaskTypesSize; i++) {
				templateBuilder.append(" or ({").append(i).append("})");
            }
            templateBuilder.append(")");
        }
		return templateBuilder.toString();
	}
}
