package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.MonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationRequestQueryService;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AerMonitoringPlanVersionsBuilderService {

    private final AerRequestQueryService aerRequestQueryService;
    private final PermitVariationRequestQueryService permitVariationRequestQueryService;

    public List<MonitoringPlanVersion> buildMonitoringPlanVersions(Long accountId, String permitId,
                                                                    Year reportingYear) {
        List<MonitoringPlanVersion> allMonitoringPlanVersions = buildMonitoringPlanVersionsForVariation(accountId,
            permitId);

        buildMonitoringPlanVersionForIssuance(accountId, permitId).ifPresent(allMonitoringPlanVersions::add);

        //we should include all versions that were active during the reporting year: flows that were completed within
        // the reporting year plus the last one of the year before
        List<MonitoringPlanVersion> monitoringPlanVersionsToIncludeInAer = allMonitoringPlanVersions.stream()
            .filter(monitoringPlanVersion -> isMonitoringPlanCompletedDuringReportingYear(reportingYear,
                monitoringPlanVersion))
            .collect(Collectors.toList());
        addPreviousMonitoringPlanVersionIfApplicable(reportingYear, allMonitoringPlanVersions,
            monitoringPlanVersionsToIncludeInAer);
        return monitoringPlanVersionsToIncludeInAer;
    }

    private List<MonitoringPlanVersion> buildMonitoringPlanVersionsForVariation(Long accountId, String permitId) {
        return permitVariationRequestQueryService.findApprovedPermitVariationRequests(accountId)
            .stream()
            .map(permitVariationRequestInfo ->
                new MonitoringPlanVersion(
                    permitId,
                    permitVariationRequestInfo.getEndDate().toLocalDate(),
                    permitVariationRequestInfo.getMetadata().getPermitConsolidationNumber()
                )
            )
            .collect(Collectors.toList());
    }

    private Optional<MonitoringPlanVersion> buildMonitoringPlanVersionForIssuance(Long accountId, String permitId) {
        return aerRequestQueryService.findEndDateOfApprovedPermitIssuanceOrTransferBByAccountId(accountId)
            .map(date -> new MonitoringPlanVersion(
                permitId,
                date.toLocalDate(),
                PermitEntity.CONSOLIDATION_NUMBER_DEFAULT_VALUE));

    }

    private static boolean isMonitoringPlanCompletedDuringReportingYear(Year reportingYear,
                                                                        MonitoringPlanVersion monitoringPlanVersion) {
        return Year.of(monitoringPlanVersion.getEndDate().getYear()).equals(reportingYear);
    }

    //we need to include the monitoring plan version that was active at the beginning of the reporting year which is
    // the last of the year before
    private static void addPreviousMonitoringPlanVersionIfApplicable(
        Year reportingYear,
        List<MonitoringPlanVersion> allMonitoringPlanVersions,
        List<MonitoringPlanVersion> monitoringPlanVersionsToIncludeInAer
    ) {
        allMonitoringPlanVersions.removeAll(monitoringPlanVersionsToIncludeInAer);
        allMonitoringPlanVersions.removeIf(monitoringPlanVersion -> Year.of(monitoringPlanVersion.getEndDate().getYear()).isAfter(reportingYear));
        if (!allMonitoringPlanVersions.isEmpty()) {
            monitoringPlanVersionsToIncludeInAer.add(allMonitoringPlanVersions.get(0));
        }
    }
}
