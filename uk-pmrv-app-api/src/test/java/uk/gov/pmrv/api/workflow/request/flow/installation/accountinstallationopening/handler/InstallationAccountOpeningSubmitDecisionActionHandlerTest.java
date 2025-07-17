package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.Decision;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.AccountOpeningDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountPayload;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.APPLICATION_ACCEPTED;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningSubmitDecisionActionHandlerTest {

    @InjectMocks
    private InstallationAccountOpeningSubmitDecisionActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    private AppUser appUser;

    @BeforeEach
    public void buildAppUser() {
        appUser = AppUser.builder().userId("user").build();
    }

    @Test
    void doProcess_accepted_decision() {
        //prepare
        final String taskProcessId = "taskProcessId";
        final Decision decision = Decision.ACCEPTED;
        RequestTask requestTask = buildMockRequestTask(taskProcessId);
        InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload payload = buildAccountSubmitDecisionPayload(decision);
        InstallationAccountPayload accountPayload = new InstallationAccountPayload();
        AccountOpeningDecisionPayload
            accountOpeningDecisionPayload = AccountOpeningDecisionPayload.builder().decision(decision).build();
        InstallationAccountOpeningDecisionRequestActionPayload accountSubmittedDecisionPayload = InstallationAccountOpeningDecisionRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_DECISION_PAYLOAD)
            .accountOpeningDecisionPayload(accountOpeningDecisionPayload)
            .build();
        InstallationAccountOpeningApprovedRequestActionPayload accountApprovedPayload = InstallationAccountOpeningApprovedRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPROVED_PAYLOAD)
            .accountPayload(accountPayload)
            .build();
//        InstallationAccountOpeningRequestPayload instAccOpeningRequestPayload = InstallationAccountOpeningRequestPayload.builder()
//            .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
//            .accountPayload(accountPayload)
//            .accountOpeningDecisionPayload(accountOpeningDecisionPayload)
//            .build();
        
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        //invoke
        handler.process(requestTask.getId(), RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION, appUser, payload);
        
        //verify
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(),
                accountSubmittedDecisionPayload,
                RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCEPTED,
                appUser.getUserId());
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(),
                accountApprovedPayload,
                RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED,
                appUser.getUserId());
        verify(workflowService, times(1))
            .completeTask(taskProcessId, Map.of(APPLICATION_ACCEPTED, Boolean.TRUE,
                    BpmnProcessConstants.APPLICATION_TYPE_IS_TRANSFER, false));
    }

    @Test
    void doProcess_rejected_decision() {
        //prepare
        final String taskProcessId = "taskProcessId";
        final Decision decision = Decision.REJECTED;
        RequestTask requestTask = buildMockRequestTask(taskProcessId);
        InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload payload = buildAccountSubmitDecisionPayload(decision);
        AccountOpeningDecisionPayload
            accountOpeningDecisionPayload = AccountOpeningDecisionPayload.builder().decision(decision).build();
        InstallationAccountOpeningDecisionRequestActionPayload accountSubmittedDecisionPayload = InstallationAccountOpeningDecisionRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_DECISION_PAYLOAD)
            .accountOpeningDecisionPayload(accountOpeningDecisionPayload)
            .build();
//        InstallationAccountOpeningRequestPayload instAccOpeningRequestPayload = InstallationAccountOpeningRequestPayload.builder()
//            .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
//            .accountPayload(new AccountPayload())
//            .accountOpeningDecisionPayload(accountOpeningDecisionPayload)
//            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        
        //invoke
        handler.process(requestTask.getId(), RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION, appUser, payload);
        
        //verify
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(),
                accountSubmittedDecisionPayload,
                RequestActionType.INSTALLATION_ACCOUNT_OPENING_REJECTED,
                appUser.getUserId());
        verify(workflowService, times(1))
            .completeTask(taskProcessId, Map.of(APPLICATION_ACCEPTED, Boolean.FALSE,
                    BpmnProcessConstants.APPLICATION_TYPE_IS_TRANSFER, false));
        verifyNoMoreInteractions(requestService);
    }

    private RequestTask buildMockRequestTask(String taskProcessId) {
        return RequestTask.builder()
                .id(1L)
                .processTaskId(taskProcessId)
                .request(Request.builder()
                    .id("1")
                    .type(RequestType.INSTALLATION_ACCOUNT_OPENING)
                    .competentAuthority(CompetentAuthorityEnum.WALES)
                    .status(RequestStatus.IN_PROGRESS)
                    .processInstanceId("process_id")
                    .payload(InstallationAccountOpeningRequestPayload.builder().build())
                    .build())
                .payload(InstallationAccountOpeningApplicationRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD)
                    .accountPayload(new InstallationAccountPayload())
                    .build())
                .build();
    }

    private InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload buildAccountSubmitDecisionPayload(Decision decision) {
        return InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload.builder()
            .accountOpeningDecisionPayload(AccountOpeningDecisionPayload.builder().decision(decision).build())
            .build();
    }

}
