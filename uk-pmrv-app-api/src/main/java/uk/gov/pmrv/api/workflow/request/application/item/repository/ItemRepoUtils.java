package uk.gov.pmrv.api.workflow.request.application.item.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@UtilityClass
class ItemRepoUtils {
	
	public BooleanExpression constructAccountRequestTaskScopeWhereClause(Map<Long, Set<RequestTaskType>> scopedAccountRequestTaskTypes,
			QRequest request, QRequestTask requestTask) {
		List<BooleanExpression> orExpressions = new ArrayList<>();
		scopedAccountRequestTaskTypes.forEach((accountId, types) -> {
        	orExpressions.add(request.accountId.eq(accountId).and(requestTask.type.in(types)));
        });
        
		return Expressions.booleanTemplate(
				ItemRepoUtils.constructMultipleOrWhereTemplate(orExpressions.size()).toString(), orExpressions);
	}
	
	public BooleanExpression constructCARequestTaskScopeWhereClause(Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedCARequestTaskTypes,
			QRequest request, QRequestTask requestTask) {
		List<BooleanExpression> orExpressions = new ArrayList<>();
		scopedCARequestTaskTypes.forEach((ca, types) -> {
        	orExpressions.add(request.competentAuthority.eq(ca).and(requestTask.type.in(types)));
        });
        
		return Expressions.booleanTemplate(
				ItemRepoUtils.constructMultipleOrWhereTemplate(orExpressions.size()).toString(), orExpressions);
	}

	private String constructMultipleOrWhereTemplate(int scopedRequestTaskTypesSize) {
		StringBuilder templateBuilder = new StringBuilder();
		if(scopedRequestTaskTypesSize == 0) {
        	templateBuilder = new StringBuilder("(1 = -1)");
        } else {
        	templateBuilder = new StringBuilder("(({0})");
            for (int i = 1; i < scopedRequestTaskTypesSize; i++) {
            	templateBuilder.append(" or ({" + i + "})");
            }
            templateBuilder.append(")");
        }
		return templateBuilder.toString();
	}
}
