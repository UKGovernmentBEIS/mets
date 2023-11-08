package uk.gov.pmrv.api.mireport.installation.executedactions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestAction;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.mireport.common.executedActions.ExecutedRequestActionsReportGenerator;
import uk.gov.pmrv.api.mireport.installation.InstallationMiReportGeneratorHandler;

import jakarta.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallationExecutedRequestActionsReportGeneratorHandler
    extends ExecutedRequestActionsReportGenerator
    implements InstallationMiReportGeneratorHandler<ExecutedRequestActionsMiReportParams> {

    private final InstallationExecutedRequestActionsRepository executedRequestActionsRepository;

    @Override
    public List<ExecutedRequestAction> findExecutedRequestActions(EntityManager entityManager,
                                                                  ExecutedRequestActionsMiReportParams reportParams) {
        return executedRequestActionsRepository.findExecutedRequestActions(entityManager, reportParams);
    }
}
