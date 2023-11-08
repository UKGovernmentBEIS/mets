package uk.gov.pmrv.api.reporting.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportableEmissionsCalculationService {

    private final List<ApproachEmissionsCalculationService> approachEmissionsCalculationServices;

    public BigDecimal calculateYearEmissions(AerContainer aerContainer) {
        AerVerificationReport verificationReport = aerContainer.getVerificationReport();
        BigDecimal totalReportableEmissions;

        if(!ObjectUtils.isEmpty(verificationReport)
                && !verificationReport.getVerificationData().getOpinionStatement().getOperatorEmissionsAcceptable()){

            totalReportableEmissions = verificationReport.getVerificationData().getOpinionStatement()
                    .getMonitoringApproachTypeEmissions().getReportableEmissions();
        }
        else {
            totalReportableEmissions = aerContainer.getAer().getMonitoringApproachEmissions().getMonitoringApproachEmissions().values().stream()
                    .map(approachEmissions -> {
                        Optional<ApproachEmissionsCalculationService> calculateApproachEmissionsService = approachEmissionsCalculationServices
                                .stream().filter(service -> approachEmissions.getType().equals(service.getType())).findFirst();

                        return calculateApproachEmissionsService.isPresent()
                                ? calculateApproachEmissionsService.get().getTotalEmissions(approachEmissions)
                                : BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        }

        return totalReportableEmissions.setScale(5, RoundingMode.HALF_UP);
    }
}
