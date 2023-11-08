package uk.gov.pmrv.api.aviationreporting.ukets.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationAerReportableEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsReportableEmissionsCalculationService implements AviationAerReportableEmissionsCalculationService<AviationAerUkEtsContainer, AviationAerUkEtsTotalReportableEmissions> {

    private final AviationAerUkEtsSubmittedEmissionsCalculationService emissionsCalculationService;

    @Override
    public AviationAerUkEtsTotalReportableEmissions calculateReportableEmissions(AviationAerUkEtsContainer aerContainer) {
        AviationAerUkEtsVerificationReport verificationReport = aerContainer.getVerificationReport();

        return AviationAerUkEtsTotalReportableEmissions.builder()
            .reportableEmissions(
                ObjectUtils.isNotEmpty(verificationReport) && Boolean.FALSE.equals(verificationReport.getVerificationData().getOpinionStatement().getEmissionsCorrect())
                    ? verificationReport.getVerificationData().getOpinionStatement().getManuallyProvidedEmissions()
                    : emissionsCalculationService.calculateTotalSubmittedEmissions(aerContainer.getAer())
            ).build();
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.UK_ETS_AVIATION;
    }
}
