package uk.gov.pmrv.api.mireport.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportParams;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportSearchResult;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MiReportService {

    private final List<MiReportGeneratorService> miReportGeneratorServices;
    private final MiReportRepository miReportRepository;

    public List<MiReportSearchResult> findByCompetentAuthorityAndAccountType(CompetentAuthorityEnum competentAuthority,
                                                                              AccountType accountType) {
        return miReportRepository.findByCompetentAuthorityAndAccountType(competentAuthority, accountType);
    }

    @Transactional(readOnly = true)
    public MiReportResult generateReport(CompetentAuthorityEnum competentAuthority, AccountType accountType,
                                         MiReportParams reportParams) {
        return getMiReportGeneratorService(accountType)
            .map(service -> service.generateReport(competentAuthority, reportParams))
            .orElseThrow(() -> new BusinessException(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED));
    }

    private Optional<MiReportGeneratorService> getMiReportGeneratorService(AccountType accountType) {
        return miReportGeneratorServices.stream()
            .filter(service -> accountType.equals(service.getAccountType()))
            .findFirst();
    }
}
