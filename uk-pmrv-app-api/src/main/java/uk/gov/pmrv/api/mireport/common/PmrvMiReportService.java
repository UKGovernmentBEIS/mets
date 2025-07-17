package uk.gov.pmrv.api.mireport.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.MiReportGeneratorHandler;
import uk.gov.netz.api.mireport.domain.MiReportParams;
import uk.gov.netz.api.mireport.domain.MiReportResult;
import uk.gov.netz.api.mireport.domain.MiReportSearchResult;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PmrvMiReportService<T extends MiReportParams, U extends MiReportGeneratorHandler<T>> {

    private final List<PmrvMiReportGeneratorService<T, U>> pmrvMiReportGeneratorServices;
    private final PmrvMiReportRepository pmrvMiReportRepository;

    public List<MiReportSearchResult> findByCompetentAuthorityAndAccountType(CompetentAuthorityEnum competentAuthority,
                                                                             AccountType accountType) {
        return pmrvMiReportRepository.findByCompetentAuthorityAndAccountType(competentAuthority, accountType);
    }

    @Transactional(readOnly = true)
    public MiReportResult generateReport(CompetentAuthorityEnum competentAuthority, AccountType accountType, T reportParams) {
        return getMiReportGeneratorService(accountType)
                .filter(generator -> pmrvMiReportRepository.findByCompetentAuthorityAndAccountType(competentAuthority, accountType)
                        .stream()
                        .anyMatch(miReportSearchResult -> miReportSearchResult.getMiReportType().equals(reportParams.getReportType())))
                .map(service -> service.generateReport(competentAuthority, reportParams))
                .orElseThrow(() -> new BusinessException(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED));
    }

    private Optional<PmrvMiReportGeneratorService<T, U>> getMiReportGeneratorService(AccountType accountType) {
        return pmrvMiReportGeneratorServices.stream()
                .filter(service -> accountType.equals(service.getAccountType()))
                .findFirst();
    }
}
