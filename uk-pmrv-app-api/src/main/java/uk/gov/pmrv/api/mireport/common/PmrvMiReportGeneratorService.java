package uk.gov.pmrv.api.mireport.common;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import uk.gov.netz.api.mireport.MiReportGeneratorHandler;
import uk.gov.netz.api.mireport.MiReportGeneratorService;
import uk.gov.netz.api.mireport.MiReportRepository;
import uk.gov.netz.api.mireport.domain.MiReportParams;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.List;


public abstract class PmrvMiReportGeneratorService<T extends MiReportParams, U extends MiReportGeneratorHandler<T>> extends MiReportGeneratorService<T, U> {

    public PmrvMiReportGeneratorService(@Nullable @Qualifier("reportEaEntityManager") EntityManager reportEaEntityManager,
                                    @Nullable @Qualifier("reportSepaEntityManager") EntityManager reportSepaEntityManager,
                                    @Nullable @Qualifier("reportNieaEntityManager") EntityManager reportNieaEntityManager,
                                    @Nullable @Qualifier("reportOpredEntityManager") EntityManager reportOpredEntityManager,
                                    @Nullable @Qualifier("reportNrwEntityManager") EntityManager reportNrwEntityManager,
                                    MiReportRepository miReportRepository,
                                    List<U> miReportGeneratorHandlers) {
        super(reportEaEntityManager, reportSepaEntityManager, reportNieaEntityManager, reportOpredEntityManager, reportNrwEntityManager, miReportRepository, miReportGeneratorHandlers);

    }

    public abstract AccountType getAccountType();
}
