package uk.gov.pmrv.api.aviationreporting.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationReportableEmissionsDTO {

    @JsonUnwrapped
    private AviationAerTotalReportableEmissions totalReportableEmissions;

    private boolean isExempted;
}
