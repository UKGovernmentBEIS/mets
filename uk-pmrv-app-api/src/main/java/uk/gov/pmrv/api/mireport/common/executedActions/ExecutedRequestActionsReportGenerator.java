package uk.gov.pmrv.api.mireport.common.executedActions;

import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;

import jakarta.persistence.EntityManager;
import java.util.List;

public abstract class ExecutedRequestActionsReportGenerator {

    public abstract List<ExecutedRequestAction> findExecutedRequestActions(EntityManager entityManager,
                                                                           ExecutedRequestActionsMiReportParams reportParams);

    public MiReportType getReportType() {
        return MiReportType.COMPLETED_WORK;
    }

    public MiReportResult generateMiReport(EntityManager entityManager, ExecutedRequestActionsMiReportParams reportParams) {
        return ExecutedRequestActionsMiReportResult.builder()
            .reportType(getReportType())
            .columnNames(ExecutedRequestAction.getColumnNames())
            .results(findExecutedRequestActions(entityManager, reportParams))
            .build();
    }
}
