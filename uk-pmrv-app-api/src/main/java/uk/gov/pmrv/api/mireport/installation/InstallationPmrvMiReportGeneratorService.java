package uk.gov.pmrv.api.mireport.installation;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.MiReportRepository;
import uk.gov.netz.api.mireport.domain.MiReportParams;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.common.PmrvMiReportGeneratorService;

import java.util.List;

@Service
public class InstallationPmrvMiReportGeneratorService<T extends MiReportParams> extends PmrvMiReportGeneratorService<T, InstallationMiReportGeneratorHandler<T>> {

    public InstallationPmrvMiReportGeneratorService(@Nullable @Qualifier("reportEaEntityManager") EntityManager reportEaEntityManager,
                                                    @Nullable @Qualifier("reportSepaEntityManager") EntityManager reportSepaEntityManager,
                                                    @Nullable @Qualifier("reportNieaEntityManager") EntityManager reportNieaEntityManager,
                                                    @Nullable @Qualifier("reportOpredEntityManager") EntityManager reportOpredEntityManager,
                                                    @Nullable @Qualifier("reportNrwEntityManager") EntityManager reportNrwEntityManager,
                                                    MiReportRepository miReportRepository,
                                                    List<InstallationMiReportGeneratorHandler<T>> installationMiReportGeneratorHandlers) {
        super(reportEaEntityManager, reportSepaEntityManager, reportNieaEntityManager, reportOpredEntityManager, reportNrwEntityManager, miReportRepository, installationMiReportGeneratorHandlers);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.INSTALLATION;
    }
}
