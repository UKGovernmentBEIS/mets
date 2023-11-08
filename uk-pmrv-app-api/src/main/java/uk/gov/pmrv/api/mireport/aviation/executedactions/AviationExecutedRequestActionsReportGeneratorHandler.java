package uk.gov.pmrv.api.mireport.aviation.executedactions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.mireport.aviation.AviationMiReportGeneratorHandler;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestAction;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsReportGenerator;

import jakarta.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AviationExecutedRequestActionsReportGeneratorHandler
    extends ExecutedRequestActionsReportGenerator
    implements AviationMiReportGeneratorHandler<ExecutedRequestActionsMiReportParams> {

    private final AviationExecutedRequestActionsRepository executedRequestActionsRepository;

    @Override
    public List<ExecutedRequestAction> findExecutedRequestActions(EntityManager entityManager,
                                                                  ExecutedRequestActionsMiReportParams reportParams) {
        return executedRequestActionsRepository.findExecutedRequestActions(entityManager, reportParams);
    }
}
