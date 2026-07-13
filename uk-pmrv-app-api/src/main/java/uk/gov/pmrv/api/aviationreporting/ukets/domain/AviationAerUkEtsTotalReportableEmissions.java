package uk.gov.pmrv.api.aviationreporting.ukets.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AviationAerUkEtsTotalReportableEmissions extends AviationAerTotalReportableEmissions {
}
