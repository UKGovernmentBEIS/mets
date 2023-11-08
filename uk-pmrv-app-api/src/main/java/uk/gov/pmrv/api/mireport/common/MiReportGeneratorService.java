package uk.gov.pmrv.api.mireport.common;

import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportParams;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;

public interface MiReportGeneratorService {

    MiReportResult generateReport(CompetentAuthorityEnum competentAuthority, MiReportParams reportParams);

    AccountType getAccountType();
}
