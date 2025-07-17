package uk.gov.pmrv.api.aviationreporting.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AviationReportableEmissionsSaveParams {

    private Long accountId;
    private Year year;
    private AviationAerTotalReportableEmissions reportableEmissions;
    private boolean isFromDre;
    private boolean isFromRegulator;
    private boolean isExempted;
}
