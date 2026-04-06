package uk.gov.pmrv.api.mireport.system.common;

import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.system.MiReportSystemGenerator;
import uk.gov.netz.api.mireport.system.MiReportSystemGeneratorDelegator;
import uk.gov.netz.api.mireport.system.MiReportSystemResult;
import uk.gov.netz.api.mireport.system.MiReportSystemParams;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.List;


public abstract class PmrvMiReportSystemGeneratorService<T extends MiReportSystemParams, U extends MiReportSystemGenerator<T>> {

	private final MiReportSystemGeneratorDelegator miReportSystemGeneratorDelegator;
	private final List<U> miReportSystemGenerators;
	
    public PmrvMiReportSystemGeneratorService(
    		MiReportSystemGeneratorDelegator miReportSystemGeneratorDelegator,
    		List<U> miReportSystemGenerators
    		) {
    	this.miReportSystemGeneratorDelegator = miReportSystemGeneratorDelegator;
    	this.miReportSystemGenerators = miReportSystemGenerators;
    }
    
    public MiReportSystemResult generateReport(CompetentAuthorityEnum competentAuthority, T reportParams) {
    	return miReportSystemGeneratorDelegator.generateReport(competentAuthority, reportParams, miReportSystemGenerators);
    }

    public abstract AccountType getAccountType();
}
