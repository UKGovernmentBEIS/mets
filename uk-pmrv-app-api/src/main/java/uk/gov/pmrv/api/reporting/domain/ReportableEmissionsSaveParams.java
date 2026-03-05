package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Year;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportableEmissionsSaveParams {

    private Long accountId;
    private Year year;
    private BigDecimal reportableEmissions;
    private boolean isFromDre;
    private  boolean isFromRegulator;
}
