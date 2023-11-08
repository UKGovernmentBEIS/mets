package uk.gov.pmrv.api.mireport.installation.outstandingrequesttasks;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.common.outstandingrequesttasks.OutstandingRegulatorRequestTasksMiReportParams;
import uk.gov.pmrv.api.mireport.common.outstandingrequesttasks.OutstandingRequestTasksReportGenerator;
import uk.gov.pmrv.api.mireport.common.outstandingrequesttasks.OutstandingRequestTasksReportService;
import uk.gov.pmrv.api.mireport.common.outstandingrequesttasks.OutstandingRequestTasksRepository;
import uk.gov.pmrv.api.mireport.installation.InstallationMiReportGeneratorHandler;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@Service
public class InstallationOutstandingRequestTasksReportGeneratorHandler
    extends OutstandingRequestTasksReportGenerator
    implements InstallationMiReportGeneratorHandler<OutstandingRegulatorRequestTasksMiReportParams> {


    public InstallationOutstandingRequestTasksReportGeneratorHandler(OutstandingRequestTasksReportService outstandingRequestTasksReportService,
                                                                     OutstandingRequestTasksRepository outstandingRequestTasksRepository,
                                                                     UserAuthService userAuthService) {
        super(outstandingRequestTasksReportService, outstandingRequestTasksRepository, userAuthService);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.INSTALLATION;
    }
}
