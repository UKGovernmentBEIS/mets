package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.MonitoringMethodologyPlans;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermitTransferBApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestRepository requestRepository;
    private final PermitQueryService permitQueryService;
    
    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();
        final String relatedRequestId = requestPayload.getRelatedRequestId();
        final Long transferringAccountId = requestRepository.findById(relatedRequestId).map(Request::getAccountId).orElseThrow(
            () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
        );
        final PermitEntityDto permitEntityDto = permitQueryService.getPermitByAccountId(transferringAccountId);
        final PermitContainer permitContainer = permitEntityDto.getPermitContainer();
        final Permit permit = permitContainer.getPermit();
        this.clearAttachments(permit);

        final Long accountId = request.getAccountId();
        final InstallationOperatorDetails installationOperatorDetails =
            installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId);

        return PermitTransferBApplicationRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_SUBMIT_PAYLOAD)
            .permitTransferDetails(requestPayload.getPermitTransferDetails())
            .permitAttachments(getPermitAttachmentsToTransfer(requestPayload, permitContainer))
            .permitType(permitContainer.getPermitType())
            .permit(permit)
            .installationOperatorDetails(installationOperatorDetails)
            .build();
    }

    private void clearAttachments(final Permit permit) {

        if (permit.getMonitoringMethodologyPlans() != null) {
            permit.getMonitoringMethodologyPlans().setPlans(null);
        }
        permit.setSiteDiagrams(null);
        permit.setAdditionalDocuments(null);
        permit.setUncertaintyAnalysis(null);
        permit.setConfidentialityStatement(null);

        if (permit.getManagementProcedures().getDataFlowActivities() != null) {
            permit.getManagementProcedures().getDataFlowActivities().setDiagramAttachmentId(null);
        }
        if (permit.getManagementProcedures().getMonitoringReporting() != null &&
                permit.getManagementProcedures().getMonitoringReporting().getOrganisationCharts() != null) {
            permit.getManagementProcedures().getMonitoringReporting().getOrganisationCharts().clear();
        }
        if (permit.getManagementProcedures().getAssessAndControlRisk() != null &&
                permit.getManagementProcedures().getAssessAndControlRisk().getRiskAssessmentAttachments() != null) {
            permit.getManagementProcedures().getAssessAndControlRisk().getRiskAssessmentAttachments().clear();
        }

        permit.getMonitoringApproaches().getMonitoringApproaches().values()
                .forEach(PermitMonitoringApproachSection::clearAttachmentIds);
    }

    private Map<UUID, String> getPermitAttachmentsToTransfer(PermitTransferBRequestPayload requestPayload, PermitContainer permitContainer) {
        Set<UUID> permitUuidsToTransfer =
                Optional.of(permitContainer).map(PermitContainer::getPermit).map(Permit::getMonitoringMethodologyPlans).map(
                                MonitoringMethodologyPlans::getDigitizedPlan).map(DigitizedPlan::getAttachmentIds)
                        .orElse(new HashSet<>());

        return Stream.concat(requestPayload.getPermitAttachments().entrySet().stream(),
                permitContainer.getPermitAttachments().entrySet().stream()
                        .filter(entry -> permitUuidsToTransfer.contains(entry.getKey()))
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_SUBMIT);
    }
}
