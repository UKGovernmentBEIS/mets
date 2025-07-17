package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service.EmpVariationUkEtsRequestQueryService;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsBuildMonitoringPlanVersionsService {
	
	private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	private final EmpVariationUkEtsRequestQueryService empVariationRequestQueryService;
	private final AviationAerRequestQueryService aerRequestQueryService;

	public List<AviationAerMonitoringPlanVersion> build(Long accountId, Year reportingYear) {
		final Optional<String> empIdOptional = emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId);
		
		if (empIdOptional.isEmpty()) {
			return Collections.emptyList();
		}
		
		final String empId = empIdOptional.get();
		final List<AviationAerMonitoringPlanVersion> monitoringPlanVersions = new ArrayList<>();
		monitoringPlanVersions.addAll(findMonitoringPlanVersionsForVariation(accountId, empId));
		findMonitoringPlanVersionForIssuance(accountId, empId).ifPresent(monitoringPlanVersions::add);
		
		final List<AviationAerMonitoringPlanVersion> monitoringPlanVersionsToIncludeInAer = monitoringPlanVersions.stream()
                .filter(monitoringPlanVersion -> isMonitoringPlanCompletedDuringReportingYear(reportingYear, monitoringPlanVersion))
                .collect(Collectors.toList());
		addPreviousMonitoringPlanVersionIfApplicable(reportingYear, monitoringPlanVersions, monitoringPlanVersionsToIncludeInAer);
		
		return monitoringPlanVersionsToIncludeInAer.stream()
				.sorted(Comparator.comparing(AviationAerMonitoringPlanVersion::getEmpApprovalDate)).toList();
	}
	
	private List<AviationAerMonitoringPlanVersion> findMonitoringPlanVersionsForVariation(Long accountId, String empId) {
        return empVariationRequestQueryService.findApprovedVariationRequests(accountId).stream()
                .map(empVariationRequestInfo -> AviationAerMonitoringPlanVersion.builder()
                        .empId(empId)
                        .empApprovalDate(empVariationRequestInfo.getEndDate().toLocalDate())
                        .empConsolidationNumber(empVariationRequestInfo.getMetadata().getEmpConsolidationNumber())
                        .build())
                .collect(Collectors.toList());

    }

    private Optional<AviationAerMonitoringPlanVersion> findMonitoringPlanVersionForIssuance(Long accountId, String empId) {
        return aerRequestQueryService.findEndDateOfApprovedEmpIssuanceByAccountId(accountId, RequestType.EMP_ISSUANCE_UKETS)
                .map(localDateTime -> AviationAerMonitoringPlanVersion.builder()
                        .empId(empId)
                        .empApprovalDate(localDateTime.toLocalDate())
                        .empConsolidationNumber(EmissionsMonitoringPlanEntity.CONSOLIDATION_NUMBER_DEFAULT_VALUE)
                        .build());
    }

    private boolean isMonitoringPlanCompletedDuringReportingYear(Year reportingYear, AviationAerMonitoringPlanVersion monitoringPlanVersion) {
        return Year.of(monitoringPlanVersion.getEmpApprovalDate().getYear()).equals(reportingYear);
    }

    private void addPreviousMonitoringPlanVersionIfApplicable(Year reportingYear, List<AviationAerMonitoringPlanVersion> allMonitoringPlanVersions,
                                                              List<AviationAerMonitoringPlanVersion> monitoringPlanVersionsToIncludeInAer) {
        allMonitoringPlanVersions.removeAll(monitoringPlanVersionsToIncludeInAer);
        allMonitoringPlanVersions.removeIf(monitoringPlanVersion -> Year.of(monitoringPlanVersion.getEmpApprovalDate().getYear()).isAfter(reportingYear));
        if (!allMonitoringPlanVersions.isEmpty()) {
            monitoringPlanVersionsToIncludeInAer.add(allMonitoringPlanVersions.get(0));
        }
    }
}
