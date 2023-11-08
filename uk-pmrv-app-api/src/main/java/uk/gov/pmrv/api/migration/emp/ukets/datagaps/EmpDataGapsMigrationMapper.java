package uk.gov.pmrv.api.migration.emp.ukets.datagaps;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.datagaps.EmpDataGaps;

@UtilityClass
public class EmpDataGapsMigrationMapper {

    public static EmpDataGaps toEmpDataGaps(EtsEmpDataGaps etsEmpDataGaps) {
        return EmpDataGaps.builder()
            .dataGaps(etsEmpDataGaps.getThresholdDataGap())
            .secondaryDataSources(etsEmpDataGaps.getSecondaryDataGap())
            .substituteData(etsEmpDataGaps.getAlternativeMethodUsed())
            .otherDataGapsTypes(etsEmpDataGaps.getDataGapMethodology())
            .build();
    }
}
