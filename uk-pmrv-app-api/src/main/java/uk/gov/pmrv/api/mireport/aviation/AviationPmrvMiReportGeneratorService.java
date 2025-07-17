package uk.gov.pmrv.api.mireport.aviation;

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
public class AviationPmrvMiReportGeneratorService<T extends MiReportParams> extends PmrvMiReportGeneratorService<T, AviationMiReportGeneratorHandler<T>> {

    public AviationPmrvMiReportGeneratorService(@Nullable @Qualifier("reportEaEntityManager") EntityManager reportEaEntityManager,
                                                @Nullable @Qualifier("reportSepaEntityManager") EntityManager reportSepaEntityManager,
                                                @Nullable @Qualifier("reportNieaEntityManager") EntityManager reportNieaEntityManager,
                                                @Nullable @Qualifier("reportOpredEntityManager") EntityManager reportOpredEntityManager,
                                                @Nullable @Qualifier("reportNrwEntityManager") EntityManager reportNrwEntityManager,
                                                MiReportRepository miReportRepository,
                                                List<AviationMiReportGeneratorHandler<T>> aviationMiReportGeneratorHandlers) {
        super(reportEaEntityManager, reportSepaEntityManager, reportNieaEntityManager, reportOpredEntityManager, reportNrwEntityManager, miReportRepository, aviationMiReportGeneratorHandlers);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.AVIATION;
    }
}
