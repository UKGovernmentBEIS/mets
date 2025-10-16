package uk.gov.pmrv.api.workflow.request.application.item.domain;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import lombok.Getter;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequestTask;

import java.time.LocalDate;

@Getter
public enum ItemOrderBy {
    NEWEST_FIRST(QRequestTask.requestTask.startDate.desc()),
    NEAREST_DUE_DATE(Expressions.numberTemplate(
        Integer.class,
        "COALESCE({0}, {1}) - COALESCE({2}, CURRENT_DATE)",
        QRequestTask.requestTask.dueDate,
        LocalDate.now().plusYears(10),
        QRequestTask.requestTask.pauseDate
        ).asc()
    );

    private final OrderSpecifier<?> orderSpecifier;

    ItemOrderBy(OrderSpecifier<?> orderSpecifier) {
        this.orderSpecifier = orderSpecifier;
    }

}
