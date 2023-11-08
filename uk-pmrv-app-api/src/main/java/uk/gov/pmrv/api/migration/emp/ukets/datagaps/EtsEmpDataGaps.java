package uk.gov.pmrv.api.migration.emp.ukets.datagaps;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpDataGaps {

    private String fldEmitterId;
    private String fldEmitterDisplayId;
    private String thresholdDataGap;
    private String secondaryDataGap;
    private String alternativeMethodUsed;
    private String dataGapMethodology;
}
