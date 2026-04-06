package uk.gov.pmrv.api.mireport.system.aviation;

import org.springframework.stereotype.Service;

import uk.gov.netz.api.mireport.system.MiReportSystemGeneratorDelegator;
import uk.gov.netz.api.mireport.system.MiReportSystemParams;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.system.common.PmrvMiReportSystemGeneratorService;

import java.util.List;

@Service
public class AviationPmrvMiReportGeneratorService<T extends MiReportSystemParams>
		extends PmrvMiReportSystemGeneratorService<T, AviationMiReportGeneratorHandler<T>> {

	public AviationPmrvMiReportGeneratorService(MiReportSystemGeneratorDelegator miReportSystemGeneratorDelegator,
			List<AviationMiReportGeneratorHandler<T>> aviationMiReportGeneratorHandlers) {
		super(miReportSystemGeneratorDelegator, aviationMiReportGeneratorHandlers);
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.AVIATION;
	}
}
