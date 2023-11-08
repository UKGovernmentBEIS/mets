package uk.gov.pmrv.api.migration.emp.corsia.datagaps;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpDataGapsCorsia {
	
	private String fldEmitterID;
    private String fldEmitterDisplayID;
    private String corsiaDataGapSecondarySource;
    private String corsiaDataGapHandling;
    private String corsiaDataGapProcedure;
    private Boolean corsiaDataGapOption;
    private String corsiaDataGapExplanation;
}
