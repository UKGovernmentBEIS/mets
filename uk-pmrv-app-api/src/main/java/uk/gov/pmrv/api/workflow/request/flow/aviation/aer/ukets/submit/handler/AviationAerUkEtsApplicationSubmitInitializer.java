package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.aviationreporting.ukets.EmpUkEtsOriginatedData;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service.EmpVariationUkEtsRequestQueryService;

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
public class AviationAerUkEtsApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private final AviationAccountQueryService aviationAccountQueryService;

    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    private final EmpVariationUkEtsRequestQueryService empVariationRequestQueryService;

    private final AviationAerRequestQueryService aerRequestQueryService;

    private final AccountContactQueryService accountContactQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        AviationAerRequestMetadata requestMetadata = (AviationAerRequestMetadata) request.getMetadata();
        AviationAerUkEtsRequestPayload requestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        final Optional<EmissionsMonitoringPlanUkEtsDTO> emissionsMonitoringPlanDTOOptional =
                emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(request.getAccountId());

        final Long verificationBodyId = requestPayload.isVerificationPerformed()
            ? requestPayload.getVerificationReport().getVerificationBodyId()
            : null;

        return AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                .reportingYear(requestMetadata.getYear())
                .reportingRequired(requestPayload.getReportingRequired())
                .reportingObligationDetails(requestPayload.getReportingObligationDetails())
                .aer(requestPayload.getAer())
                .aerAttachments(retrieveRequestTaskPayloadAttachments(request ,emissionsMonitoringPlanDTOOptional))
                .aerSectionsCompleted(requestPayload.getAerSectionsCompleted())
                .aerMonitoringPlanVersions(buildMonitoringPlanVersions(request.getAccountId(), requestMetadata.getYear()))
                .serviceContactDetails(getServiceContactDetails(request.getAccountId()))
                .empOriginatedData(buildEmpUkEtsOriginatedData(request, emissionsMonitoringPlanDTOOptional))
                .verificationPerformed(requestPayload.isVerificationPerformed())
                .verificationBodyId(verificationBodyId)
                .verificationSectionsCompleted(requestPayload.getVerificationSectionsCompleted())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT);
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
        return empVariationRequestQueryService.findApprovedVariationRequests(accountId).stream()
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

    private EmpUkEtsOriginatedData buildEmpUkEtsOriginatedData(Request request, Optional<EmissionsMonitoringPlanUkEtsDTO> empOptional){

        EmpUkEtsOriginatedData empUkEtsOriginatedData;

        AviationAerUkEtsRequestPayload requestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        AviationAccountInfoDTO aviationAccountInfo = aviationAccountQueryService.getAviationAccountInfoDTOById(request.getAccountId());

        if(ObjectUtils.isEmpty(requestPayload.getEmpOriginatedData())) {
            Optional<EmpOperatorDetails> empOperatorDetailsOptional = empOptional
                .map(EmissionsMonitoringPlanUkEtsDTO::getEmpContainer)
                .map(EmissionsMonitoringPlanUkEtsContainer::getEmissionsMonitoringPlan)
                .map(EmissionsMonitoringPlanUkEts::getOperatorDetails);

            empUkEtsOriginatedData = EmpUkEtsOriginatedData.builder()
                .operatorDetails(AviationOperatorDetails.builder()
                    .operatorName(aviationAccountInfo.getName())
                    .crcoCode(aviationAccountInfo.getCrcoCode())
                    .flightIdentification(empOperatorDetailsOptional.map(EmpOperatorDetails::getFlightIdentification).orElse(null))
                    .airOperatingCertificate(empOperatorDetailsOptional.map(EmpOperatorDetails::getAirOperatingCertificate).orElse(null))
                    .operatingLicense(empOperatorDetailsOptional.map(EmpOperatorDetails::getOperatingLicense).orElse(null))
                    .organisationStructure(empOperatorDetailsOptional.map(EmpOperatorDetails::getOrganisationStructure).orElse(null))
                    .build())
                .build();

        } else {
            empUkEtsOriginatedData = requestPayload.getEmpOriginatedData();
            empUkEtsOriginatedData.getOperatorDetails().setCrcoCode(aviationAccountInfo.getCrcoCode());
        }

        return empUkEtsOriginatedData;
    }

    private Map<UUID, String> retrieveRequestTaskPayloadAttachments(Request request,
                                                                    Optional<EmissionsMonitoringPlanUkEtsDTO> empOptional) {
        Map<UUID, String> aerAttachments = new HashMap<>();
        AviationAerUkEtsRequestPayload requestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        if(ObjectUtils.isEmpty(requestPayload.getAerAttachments())) {
            empOptional.ifPresent(emp -> {
                EmissionsMonitoringPlanUkEtsContainer empContainer = emp.getEmpContainer();
                EmpOperatorDetails operatorDetails = empContainer.getEmissionsMonitoringPlan().getOperatorDetails();

                Map<UUID, String> empAttachments = new HashMap<>(empContainer.getEmpAttachments());
                empAttachments.keySet().retainAll(operatorDetails.getAttachmentIds());

                aerAttachments.putAll(empAttachments);
            });
        } else {
            aerAttachments.putAll(requestPayload.getAerAttachments());
        }

        return aerAttachments;
    }

    private ServiceContactDetails getServiceContactDetails(Long accountId) {
        return accountContactQueryService.getServiceContactDetails(accountId).orElse(null);
    }
}
