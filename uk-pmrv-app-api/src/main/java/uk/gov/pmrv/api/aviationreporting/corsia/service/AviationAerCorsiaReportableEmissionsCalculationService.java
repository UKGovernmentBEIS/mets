package uk.gov.pmrv.api.aviationreporting.corsia.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationAerReportableEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaReportableEmissionsCalculationService
    implements AviationAerReportableEmissionsCalculationService<AviationAerCorsiaContainer, AviationAerCorsiaTotalReportableEmissions> {

    private final AviationAerCorsiaSubmittedEmissionsCalculationService emissionsCalculationService;

    @Override
    public AviationAerCorsiaTotalReportableEmissions calculateReportableEmissions(AviationAerCorsiaContainer aerContainer) {
        AviationAerCorsiaVerificationReport verificationReport = aerContainer.getVerificationReport();
        AviationAerCorsiaTotalEmissions operatorTotalEmissions = emissionsCalculationService.calculateTotalSubmittedEmissions(aerContainer.getAer(), aerContainer.getReportingYear());
        boolean getFromVerifier = ObjectUtils.isNotEmpty(verificationReport) && Boolean.FALSE.equals(verificationReport.getVerificationData().getOpinionStatement().getEmissionsCorrect());

        return AviationAerCorsiaTotalReportableEmissions.builder()
            .reportableEmissions(
                getFromVerifier
                    ? verificationReport.getVerificationData().getOpinionStatement().getManuallyInternationalFlightsProvidedEmissions()
                    : operatorTotalEmissions.getAllFlightsEmissions()
            )
            .reportableOffsetEmissions(
                getFromVerifier
                    ? verificationReport.getVerificationData().getOpinionStatement().getManuallyOffsettingFlightsProvidedEmissions()
                    : operatorTotalEmissions.getOffsetFlightsEmissions()
            )
            .reportableReductionClaimEmissions(operatorTotalEmissions.getEmissionsReductionClaim())
            .build();
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.CORSIA;
    }
}
