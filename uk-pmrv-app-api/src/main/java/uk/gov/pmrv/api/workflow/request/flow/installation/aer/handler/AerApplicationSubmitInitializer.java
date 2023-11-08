package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper.MonitoringApproachSourceStreamMonitoringTiersMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerSectionInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.MonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationRequestQueryService;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AerApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final PermitQueryService permitQueryService;
    private final PermitVariationRequestQueryService permitVariationRequestQueryService;

    private final AerRequestQueryService aerRequestQueryService;
    private final List<AerSectionInitializationService> aerSectionInitializationServices;

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final Long accountId = request.getAccountId();
        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
            .getInstallationOperatorDetails(accountId);
        final AerRequestPayload requestPayload = (AerRequestPayload) request.getPayload();
        final Aer aer;
        final PermitOriginatedData permitOriginatedData;

        if (ObjectUtils.isEmpty(requestPayload.getAer())) {
            final PermitContainer permitContainer = permitQueryService.getPermitContainerByAccountId(accountId);
            final InstallationCategory installationCategory =
                installationOperatorDetailsQueryService.getInstallationCategory(accountId);

            aer = initializeAer(permitContainer.getPermit());
            permitOriginatedData = initializePermitOriginatedData(accountId, aer, permitContainer,
                installationCategory);
        } else {
            aer = requestPayload.getAer();
            permitOriginatedData = requestPayload.getPermitOriginatedData();
        }

        String permitId = permitQueryService.getPermitIdByAccountId(accountId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        AerRequestMetadata aerRequestMetadata = (AerRequestMetadata) request.getMetadata();

        // Get verification body id in case of verification performed
        Long verificationBodyId = requestPayload.isVerificationPerformed()
            ? requestPayload.getVerificationReport().getVerificationBodyId()
            : null;

        return AerApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
            .installationOperatorDetails(installationOperatorDetails)
            .permitType(permitOriginatedData.getPermitType())
            .aer(aer)
            .aerAttachments(requestPayload.getAerAttachments())
            .aerSectionsCompleted(requestPayload.getAerSectionsCompleted())
            .monitoringPlanVersions(buildMonitoringPlanVersions(accountId, permitId, aerRequestMetadata.getYear()))
            .permitOriginatedData(permitOriginatedData)
            .reportingYear(aerRequestMetadata.getYear())
            .verificationPerformed(requestPayload.isVerificationPerformed())
            .verificationSectionsCompleted(requestPayload.getVerificationSectionsCompleted())
            .verificationBodyId(verificationBodyId)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AER_APPLICATION_SUBMIT);
    }

    private Aer initializeAer(Permit permit) {
        Aer aer = new Aer();
        aerSectionInitializationServices.forEach(sectionInitializer -> sectionInitializer.initialize(aer, permit));
        return aer;
    }

    private List<MonitoringPlanVersion> buildMonitoringPlanVersions(Long accountId, String permitId,
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

    private PermitOriginatedData initializePermitOriginatedData(Long accountId, Aer aer,
                                                                PermitContainer permitContainer,
                                                                InstallationCategory installationCategory) {
        return PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(
                MonitoringApproachSourceStreamMonitoringTiersMapper
                    .transformToMonitoringApproachMonitoringTiers(aer.getMonitoringApproachEmissions())
            )
            .permitNotificationIds(aerRequestQueryService.getApprovedPermitNotificationIdsByAccount(accountId))
            .permitType(permitContainer.getPermitType())
            .installationCategory(installationCategory)
            .build();
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
