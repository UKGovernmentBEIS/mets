package uk.gov.pmrv.api.mireport.aviation.executedactions;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestActionsMiReportParams;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestActionsReportGenerator;
import uk.gov.pmrv.api.mireport.aviation.AviationMiReportGeneratorHandler;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AviationExecutedRequestActionsReportGeneratorHandler
    extends ExecutedRequestActionsReportGenerator<AviationExecutedRequestAction>
    implements AviationMiReportGeneratorHandler<ExecutedRequestActionsMiReportParams> {

    private final AviationExecutedRequestActionsRepository executedRequestActionsRepository;

    @Override
    public List<AviationExecutedRequestAction> findExecutedRequestActions(EntityManager entityManager,
                                                                  ExecutedRequestActionsMiReportParams reportParams) {
        return executedRequestActionsRepository.findExecutedRequestActions(entityManager, reportParams);
    }

    @Override
    public List<String> getColumnNames() {
        return AviationExecutedRequestAction.getColumnNames();
    }
}
