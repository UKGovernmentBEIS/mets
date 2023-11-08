package uk.gov.pmrv.api.mireport.common.executedActions;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface ExecutedRequestActionsRepository {

    List<ExecutedRequestAction> findExecutedRequestActions(EntityManager entityManager,
                                                           ExecutedRequestActionsMiReportParams reportParams);
}
