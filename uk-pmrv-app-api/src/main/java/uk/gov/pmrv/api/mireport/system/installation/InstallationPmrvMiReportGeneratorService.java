package uk.gov.pmrv.api.mireport.system.installation;

import org.springframework.stereotype.Service;

import uk.gov.netz.api.mireport.system.MiReportSystemGeneratorDelegator;
import uk.gov.netz.api.mireport.system.MiReportSystemParams;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.system.common.PmrvMiReportSystemGeneratorService;

import java.util.List;

@Service
public class InstallationPmrvMiReportGeneratorService<T extends MiReportSystemParams>
		extends PmrvMiReportSystemGeneratorService<T, InstallationMiReportGeneratorHandler<T>> {

	public InstallationPmrvMiReportGeneratorService(MiReportSystemGeneratorDelegator miReportSystemGeneratorDelegator,
			List<InstallationMiReportGeneratorHandler<T>> installationMiReportGeneratorHandlers) {
		super(miReportSystemGeneratorDelegator, installationMiReportGeneratorHandlers);
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.INSTALLATION;
	}
}
