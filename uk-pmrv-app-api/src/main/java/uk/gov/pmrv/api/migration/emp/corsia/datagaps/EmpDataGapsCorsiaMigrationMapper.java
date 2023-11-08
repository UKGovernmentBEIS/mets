package uk.gov.pmrv.api.migration.emp.corsia.datagaps;

import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.datagaps.EmpDataGapsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;

public class EmpDataGapsCorsiaMigrationMapper {

	public static EmpDataGapsCorsia toEmpDataGaps(EtsEmpDataGapsCorsia etsEmpDataGaps, EmissionsMonitoringApproachTypeCorsia monitoringApproachType) {
        return EmpDataGapsCorsia.builder()
            .dataGaps(EmissionsMonitoringApproachTypeCorsia.CERT_MONITORING.equals(monitoringApproachType) 
            		? etsEmpDataGaps.getCorsiaDataGapHandling() 
            				: etsEmpDataGaps.getCorsiaDataGapProcedure())
            .secondaryDataSources(etsEmpDataGaps.getCorsiaDataGapSecondarySource())
            .secondarySourcesDataGapsExist(etsEmpDataGaps.getCorsiaDataGapOption())
            .secondarySourcesDataGapsConditions(etsEmpDataGaps.getCorsiaDataGapExplanation())
            .build();
    }
}
