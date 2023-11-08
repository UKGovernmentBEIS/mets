package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
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
            .permitAttachments(requestPayload.getPermitAttachments())
            .permitType(permitContainer.getPermitType())
            .permit(permit)
            .installationOperatorDetails(installationOperatorDetails)
            .build();
    }

    private void clearAttachments(final Permit permit) {

        permit.setMonitoringMethodologyPlans(null);
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
        permit.getMonitoringApproaches().getMonitoringApproaches().values()
            .forEach(PermitMonitoringApproachSection::clearAttachmentIds);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_SUBMIT);
    }
}
