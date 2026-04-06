package uk.gov.pmrv.api.mireport.system.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.system.MiReportSystemParams;
import uk.gov.netz.api.mireport.system.MiReportSystemResult;
import uk.gov.netz.api.mireport.system.MiReportSystemSearchResult;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PmrvMiReportSystemService {

    private final List<PmrvMiReportSystemGeneratorService> pmrvMiReportGeneratorServices;
    private final PmrvMiReportSystemRepository pmrvMiReportRepository;

    public List<MiReportSystemSearchResult> findByCompetentAuthorityAndAccountType(CompetentAuthorityEnum competentAuthority,
                                                                             AccountType accountType) {
        return pmrvMiReportRepository.findByCompetentAuthorityAndAccountType(competentAuthority, accountType);
    }

    @Transactional(readOnly = true)
    public MiReportSystemResult generateReport(CompetentAuthorityEnum competentAuthority, AccountType accountType, MiReportSystemParams reportParams) {
		boolean existsReport = pmrvMiReportRepository.existsByCompetentAuthorityAndAccountTypeAndMiReportType(
				competentAuthority, accountType, reportParams.getReportType());
		
		if(!existsReport) {
			throw new BusinessException(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED);
		}
		
		return pmrvMiReportGeneratorServices.stream()
                .filter(service -> accountType.equals(service.getAccountType()))
                .findFirst()
                .map(service -> service.generateReport(competentAuthority, reportParams))
                .orElseThrow(() -> new BusinessException(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED));
    }

}
