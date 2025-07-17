package uk.gov.pmrv.api.mireport.installation.executedactions;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestAction;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestActionsMiReportParams;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestActionsReportGenerator;
import uk.gov.pmrv.api.mireport.installation.InstallationMiReportGeneratorHandler;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallationExecutedRequestActionsReportGeneratorHandler
    extends ExecutedRequestActionsReportGenerator<InstallationExecutedRequestAction>
    implements InstallationMiReportGeneratorHandler<ExecutedRequestActionsMiReportParams> {

    private final InstallationExecutedRequestActionsRepository executedRequestActionsRepository;

    @Override
    public List<InstallationExecutedRequestAction> findExecutedRequestActions(EntityManager entityManager,
                                                                  ExecutedRequestActionsMiReportParams reportParams) {
        return executedRequestActionsRepository.findExecutedRequestActions(entityManager, reportParams);
    }

    @Override
    public List<String> getColumnNames() {
        return InstallationExecutedRequestAction.getColumnNames();
    }
}
