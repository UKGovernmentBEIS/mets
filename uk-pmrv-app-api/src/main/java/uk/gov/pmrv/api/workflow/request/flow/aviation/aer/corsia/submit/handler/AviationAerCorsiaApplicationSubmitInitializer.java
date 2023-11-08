package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.EmpCorsiaOriginatedData;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service.EmpVariationCorsiaRequestQueryService;

import java.time.Year;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private final AviationAccountQueryService aviationAccountQueryService;

    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    private final EmpVariationCorsiaRequestQueryService empVariationCorsiaRequestQueryService;

    private final AviationAerRequestQueryService aerRequestQueryService;

    private final AccountContactQueryService accountContactQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        
        final AviationAerCorsiaRequestMetadata requestMetadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();
        final AviationAerCorsiaRequestPayload requestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        final Optional<EmissionsMonitoringPlanCorsiaDTO> emissionsMonitoringPlanDTOOptional =
                emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(request.getAccountId());

        final Long verificationBodyId = requestPayload.isVerificationPerformed()
            ? requestPayload.getVerificationReport().getVerificationBodyId()
            : null;

        return AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .reportingYear(requestMetadata.getYear())
                .reportingRequired(requestPayload.getReportingRequired())
                .reportingObligationDetails(requestPayload.getReportingObligationDetails())
                .aer(requestPayload.getAer())
                .aerAttachments(retrieveRequestTaskPayloadAttachments(request ,emissionsMonitoringPlanDTOOptional))
                .aerSectionsCompleted(requestPayload.getAerSectionsCompleted())
                .aerMonitoringPlanVersions(buildMonitoringPlanVersions(request.getAccountId(), requestMetadata.getYear()))
                .serviceContactDetails(getServiceContactDetails(request.getAccountId()))
                .empOriginatedData(buildEmpCorsiaOriginatedData(request, emissionsMonitoringPlanDTOOptional))
                .verificationBodyId(verificationBodyId)
                .verificationPerformed(requestPayload.isVerificationPerformed())
                .verificationSectionsCompleted(requestPayload.getVerificationSectionsCompleted())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT);
    }

    private EmpCorsiaOriginatedData buildEmpCorsiaOriginatedData(Request request, Optional<EmissionsMonitoringPlanCorsiaDTO> empOptional){

        EmpCorsiaOriginatedData empCorsiaOriginatedData;

        AviationAerCorsiaRequestPayload requestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        if(ObjectUtils.isEmpty(requestPayload.getEmpOriginatedData())) {
            Optional<EmpCorsiaOperatorDetails> empOperatorDetailsOptional = empOptional
                .map(EmissionsMonitoringPlanCorsiaDTO::getEmpContainer)
                .map(EmissionsMonitoringPlanCorsiaContainer::getEmissionsMonitoringPlan)
                .map(EmissionsMonitoringPlanCorsia::getOperatorDetails);

            AviationAccountInfoDTO aviationAccountInfo = aviationAccountQueryService.getAviationAccountInfoDTOById(request.getAccountId());

            empCorsiaOriginatedData = EmpCorsiaOriginatedData.builder()
                .operatorDetails(AviationCorsiaOperatorDetails.builder()
                    .operatorName(aviationAccountInfo.getName())
                    .flightIdentification(empOperatorDetailsOptional.map(EmpCorsiaOperatorDetails::getFlightIdentification).orElse(null))
                    .airOperatingCertificate(empOperatorDetailsOptional.map(EmpCorsiaOperatorDetails::getAirOperatingCertificate).orElse(null))
                    .organisationStructure(empOperatorDetailsOptional.map(EmpCorsiaOperatorDetails::getOrganisationStructure).orElse(null))
                    .build())
                .build();

        } else {
            empCorsiaOriginatedData = requestPayload.getEmpOriginatedData();
        }

        return empCorsiaOriginatedData;
    }

    private Map<UUID, String> retrieveRequestTaskPayloadAttachments(Request request,
                                                                    Optional<EmissionsMonitoringPlanCorsiaDTO> empOptional) {
        Map<UUID, String> aerAttachments = new HashMap<>();
        AviationAerCorsiaRequestPayload requestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        if(ObjectUtils.isEmpty(requestPayload.getAerAttachments())) {
            empOptional.ifPresent(emp -> {
                EmissionsMonitoringPlanCorsiaContainer empContainer = emp.getEmpContainer();
                EmpCorsiaOperatorDetails operatorDetails = empContainer.getEmissionsMonitoringPlan().getOperatorDetails();

                Map<UUID, String> empAttachments = new HashMap<>(empContainer.getEmpAttachments());
                empAttachments.keySet().retainAll(operatorDetails.getAttachmentIds());

                aerAttachments.putAll(empAttachments);
            });
        } else {
            aerAttachments.putAll(requestPayload.getAerAttachments());
        }

        return aerAttachments;
    }

    private List<AviationAerMonitoringPlanVersion> buildMonitoringPlanVersions(Long accountId, Year reportingYear) {
        
        final Optional<String> empIdOptional = emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId);
        if (empIdOptional.isPresent()) {
            String empId = empIdOptional.get();
            List<AviationAerMonitoringPlanVersion> monitoringPlanVersions = findMonitoringPlanVersionsForVariation(accountId, empId);
            findMonitoringPlanVersionForIssuance(accountId, empId).ifPresent(monitoringPlanVersions::add);
            final List<AviationAerMonitoringPlanVersion> monitoringPlanVersionsToIncludeInAer = monitoringPlanVersions.stream()
                    .filter(monitoringPlanVersion -> isMonitoringPlanCompletedDuringReportingYear(reportingYear, monitoringPlanVersion))
                    .collect(Collectors.toList());
            addPreviousMonitoringPlanVersionIfApplicable(reportingYear, monitoringPlanVersions, monitoringPlanVersionsToIncludeInAer);
            return monitoringPlanVersionsToIncludeInAer.stream().sorted(Comparator.comparing(AviationAerMonitoringPlanVersion::getEmpApprovalDate)).toList();
        }
        return Collections.emptyList();
    }

    private List<AviationAerMonitoringPlanVersion> findMonitoringPlanVersionsForVariation(Long accountId, String empId) {
        
        return empVariationCorsiaRequestQueryService.findApprovedVariationRequests(accountId).stream()
                .map(empVariationRequestInfo -> AviationAerMonitoringPlanVersion.builder()
                        .empId(empId)
                        .empApprovalDate(empVariationRequestInfo.getEndDate().toLocalDate())
                        .empConsolidationNumber(empVariationRequestInfo.getMetadata().getEmpConsolidationNumber())
                        .build())
                .collect(Collectors.toList());

    }

    private Optional<AviationAerMonitoringPlanVersion> findMonitoringPlanVersionForIssuance(Long accountId, String empId) {
        return aerRequestQueryService.findEndDateOfApprovedEmpIssuanceByAccountId(accountId)
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

    private void initializeRequestTaskPayloadAttachments(AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload,
                                                        EmissionsMonitoringPlanCorsiaDTO empDTO) {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = empDTO.getEmpContainer();
        final EmpCorsiaOperatorDetails operatorDetails = empContainer.getEmissionsMonitoringPlan().getOperatorDetails();

        final Map<UUID, String> initializedAerAttachments = new HashMap<>(empContainer.getEmpAttachments());
        initializedAerAttachments.keySet().retainAll(operatorDetails.getAttachmentIds());

        requestTaskPayload.setAerAttachments(initializedAerAttachments);
    }

    private ServiceContactDetails getServiceContactDetails(Long accountId) {
        return accountContactQueryService.getServiceContactDetails(accountId).orElse(null);
    }
}
