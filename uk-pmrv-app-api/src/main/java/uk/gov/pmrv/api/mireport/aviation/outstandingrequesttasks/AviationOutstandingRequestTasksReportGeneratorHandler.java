package uk.gov.pmrv.api.mireport.aviation.outstandingrequesttasks;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRegulatorRequestTasksMiReportParams;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRequestTasksReportGenerator;
import uk.gov.pmrv.api.mireport.aviation.AviationMiReportGeneratorHandler;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;

@Service
public class AviationOutstandingRequestTasksReportGeneratorHandler
        extends OutstandingRequestTasksReportGenerator<AviationOutstandingRequestTask>
        implements AviationMiReportGeneratorHandler<OutstandingRegulatorRequestTasksMiReportParams> {

    private final AviationOutstandingRequestTasksRepository outstandingRequestTasksRepository;

    public AviationOutstandingRequestTasksReportGeneratorHandler(AviationOutstandingRequestTasksRepository outstandingRequestTasksRepository,
                                                                 AviationOutstandingRequestTasksReportService outstandingRequestTasksReportService,
                                                                 UserAuthService userAuthService) {
        super(outstandingRequestTasksReportService, userAuthService);
        this.outstandingRequestTasksRepository = outstandingRequestTasksRepository;
    }

    @Override
    public List<AviationOutstandingRequestTask> findOutstandingRequestTaskParams(EntityManager entityManager, OutstandingRegulatorRequestTasksMiReportParams reportParams) {
        return outstandingRequestTasksRepository.findOutstandingRequestTaskParams(entityManager, reportParams);
    }

    @Override
    public List<String> getColumnNames() {
        return AviationOutstandingRequestTask.getColumnNames();
    }
}
