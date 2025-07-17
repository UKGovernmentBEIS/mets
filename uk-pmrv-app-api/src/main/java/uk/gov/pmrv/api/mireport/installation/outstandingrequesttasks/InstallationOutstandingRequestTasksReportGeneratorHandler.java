package uk.gov.pmrv.api.mireport.installation.outstandingrequesttasks;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRegulatorRequestTasksMiReportParams;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRequestTasksReportGenerator;
import uk.gov.pmrv.api.mireport.installation.InstallationMiReportGeneratorHandler;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;

@Service
public class InstallationOutstandingRequestTasksReportGeneratorHandler
        extends OutstandingRequestTasksReportGenerator<InstallationOutstandingRequestTask>
        implements InstallationMiReportGeneratorHandler<OutstandingRegulatorRequestTasksMiReportParams> {

    private final InstallationOutstandingRequestTasksRepository outstandingRequestTasksRepository;

    public InstallationOutstandingRequestTasksReportGeneratorHandler(InstallationOutstandingRequestTasksRepository outstandingRequestTasksRepository,
                                                                     InstallationOutstandingRequestTasksReportService outstandingRequestTasksReportService,
                                                                     UserAuthService userAuthService) {
        super(outstandingRequestTasksReportService, userAuthService);
        this.outstandingRequestTasksRepository = outstandingRequestTasksRepository;
    }

    @Override
    public List<InstallationOutstandingRequestTask> findOutstandingRequestTaskParams(EntityManager entityManager, OutstandingRegulatorRequestTasksMiReportParams reportParams) {
        return outstandingRequestTasksRepository.findOutstandingRequestTaskParams(entityManager, reportParams);
    }

    @Override
    public List<String> getColumnNames() {
        return InstallationOutstandingRequestTask.getColumnNames();
    }
}
