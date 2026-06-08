package uk.gov.pmrv.api.mireport.system.aviation.userreportentry;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.mireport.system.EmptyMiReportSystemParams;
import uk.gov.netz.api.mireport.system.MiReportSystemResult;
import uk.gov.pmrv.api.mireport.system.common.PmrvMiReportResultTypes;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.system.aviation.AviationMiReportGeneratorHandler;
import uk.gov.pmrv.api.mireport.system.common.userreportentry.UserReportEntry;
import uk.gov.pmrv.api.mireport.system.common.userreportentry.UserReportEntryRepository;
import uk.gov.pmrv.api.mireport.system.common.userreportentry.UserReportEntryGeneratorHandler;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;

@Service
public class AviationUserReportEntryGeneratorHandler extends UserReportEntryGeneratorHandler
    implements AviationMiReportGeneratorHandler<EmptyMiReportSystemParams> {

    public AviationUserReportEntryGeneratorHandler(UserAuthService userAuthService, UserReportEntryRepository userReportEntryRepository, AuthorityRepository authorityRepository) {
        super(userAuthService, userReportEntryRepository, authorityRepository);
    }

    @Override
    public MiReportSystemResult generateMiReport(EntityManager entityManager, EmptyMiReportSystemParams reportParams) {
        List<UserReportEntry> payload = generate(entityManager, AccountType.AVIATION);
        return buildResult(payload, getReportType());
    }

    @Override
    public String getReportType() {
        return PmrvMiReportResultTypes.LIST_OF_USER_REPORT_ENTRIES.toString();
    }
}
