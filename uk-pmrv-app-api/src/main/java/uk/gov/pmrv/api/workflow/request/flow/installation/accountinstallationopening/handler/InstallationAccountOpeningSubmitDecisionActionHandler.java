package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.Decision;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.AccountOpeningDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.mapper.InstallationAccountPayloadMapper;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InstallationAccountOpeningSubmitDecisionActionHandler
        implements RequestTaskActionHandler<InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload> {

    private final WorkflowService workflowService;
    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private static final InstallationAccountPayloadMapper INSTALLATION_ACCOUNT_PAYLOAD_MAPPER = Mappers.getMapper(InstallationAccountPayloadMapper.class);

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload payload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        Request request = requestTask.getRequest();
        AccountOpeningDecisionPayload
                accountOpeningDecisionPayload = payload.getAccountOpeningDecisionPayload();
        boolean isApplicationAccepted = isApplicationAccepted(accountOpeningDecisionPayload);

        // Add request actions
        requestService.addActionToRequest(
                request,
                buildSubmittedDecisionPayload(accountOpeningDecisionPayload),
                isApplicationAccepted ?
                        RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCEPTED :
                        RequestActionType.INSTALLATION_ACCOUNT_OPENING_REJECTED,
                appUser.getUserId());

        InstallationAccountOpeningApplicationRequestTaskPayload accountApplyRequestTaskPayload =
                (InstallationAccountOpeningApplicationRequestTaskPayload) requestTask.getPayload();

        if (isApplicationAccepted) {
            requestService.addActionToRequest(
                    request,
                    INSTALLATION_ACCOUNT_PAYLOAD_MAPPER.toInstallationAccountOpeningApprovedRequestActionPayload(accountApplyRequestTaskPayload.getAccountPayload()),
                    RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED,
                    appUser.getUserId());
        }

        updateRequestPayload(request, accountApplyRequestTaskPayload.getAccountPayload(), accountOpeningDecisionPayload);

        // Complete task
        final boolean isTransfer = ApplicationType.TRANSFER.equals(accountApplyRequestTaskPayload.getAccountPayload().getApplicationType());
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.APPLICATION_ACCEPTED, isApplicationAccepted,
                        BpmnProcessConstants.APPLICATION_TYPE_IS_TRANSFER, isTransfer)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION);
    }

    private boolean isApplicationAccepted(AccountOpeningDecisionPayload payload) {
        return payload.getDecision() == Decision.ACCEPTED;
    }

    private InstallationAccountOpeningDecisionRequestActionPayload buildSubmittedDecisionPayload(
            AccountOpeningDecisionPayload accountOpeningDecisionPayload) {
        return InstallationAccountOpeningDecisionRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_DECISION_PAYLOAD)
                .accountOpeningDecisionPayload(accountOpeningDecisionPayload)
                .build();
    }

    private void updateRequestPayload(Request request, InstallationAccountPayload accountPayload, AccountOpeningDecisionPayload decisionPayload) {
        InstallationAccountOpeningRequestPayload instAccOpeningRequestPayload = (InstallationAccountOpeningRequestPayload) request.getPayload();
        instAccOpeningRequestPayload.setAccountPayload(accountPayload);
        instAccOpeningRequestPayload.setAccountOpeningDecisionPayload(decisionPayload);
    }
}
