package uk.gov.pmrv.api.aviationreporting.ukets.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AviationAerUkEtsTotalReportableEmissions extends AviationAerTotalReportableEmissions {
}
