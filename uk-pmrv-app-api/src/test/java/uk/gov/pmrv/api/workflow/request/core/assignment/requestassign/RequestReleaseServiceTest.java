package uk.gov.pmrv.api.workflow.request.core.assignment.requestassign;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW;

@ExtendWith(MockitoExtension.class)
class RequestReleaseServiceTest {

    @InjectMocks
    private RequestReleaseService service;
    
    @Mock
    private AuthorizationRulesQueryService authorizationRulesQueryService;

    @Test
    void releaseRequest_operator_task() {
        final String operatorAssignee = "operatorAssignee";
        final String regulatorAssignee = "regulatorAssignee";
        Request request = Request.builder()
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .operatorAssignee(operatorAssignee)
                .regulatorAssignee(regulatorAssignee)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
            .assignee(operatorAssignee)
            .build();

        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
            .thenReturn(Optional.of(RoleTypeConstants.OPERATOR));

        service.releaseRequest(requestTask);
        assertEquals(regulatorAssignee, request.getPayload().getRegulatorAssignee());

        verify(authorizationRulesQueryService, times(1))
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
    }

    @Test
    void releaseRequest_operator_task_without_assignee() {
        final String operatorAssignee = "operatorAssignee";
        final String regulatorAssignee = "regulatorAssignee";
        Request request = Request.builder()
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .operatorAssignee(operatorAssignee)
                .regulatorAssignee(regulatorAssignee)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
            .build();

        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
            .thenReturn(Optional.of(RoleTypeConstants.OPERATOR));

        service.releaseRequest(requestTask);

        assertNull(request.getPayload().getOperatorAssignee());
        assertEquals(regulatorAssignee, request.getPayload().getRegulatorAssignee());

        verify(authorizationRulesQueryService, times(1))
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
    }

    @Test
    void releaseRequest_operator_request_operator_assignee_other_than_task_assignee() {
        final String operatorAssignee = "operatorAssignee";
        Request request = Request.builder()
            .payload(InstallationAccountOpeningRequestPayload.builder().operatorAssignee(operatorAssignee).build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
            .assignee("assignee")
            .build();

        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
            .thenReturn(Optional.of(RoleTypeConstants.OPERATOR));

        service.releaseRequest(requestTask);

        verify(authorizationRulesQueryService, times(1))
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
    }

    @Test
    void releaseRequest_operator_request_without_operator_assignee() {
        final String regulatorAssignee = "regulatorAssignee";
        Request request = Request.builder()
            .payload(InstallationAccountOpeningRequestPayload.builder().regulatorAssignee(regulatorAssignee).build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
            .assignee("assignee")
            .build();

        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
            .thenReturn(Optional.of(RoleTypeConstants.OPERATOR));

        service.releaseRequest(requestTask);

        verify(authorizationRulesQueryService, times(1))
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
    }

    @Test
    void releaseRequest_regulator_task() {
        final String operatorAssignee = "operatorAssignee";
        final String regulatorAssignee = "regulatorAssignee";
        Request request = Request.builder()
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .operatorAssignee(operatorAssignee)
                .regulatorAssignee(regulatorAssignee)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW)
            .assignee(regulatorAssignee)
            .build();

        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
            .thenReturn(Optional.of(RoleTypeConstants.REGULATOR));

        service.releaseRequest(requestTask);

        assertNull(request.getPayload().getRegulatorAssignee());
        assertEquals(operatorAssignee, request.getPayload().getOperatorAssignee());

        verify(authorizationRulesQueryService, times(1))
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
    }

    @Test
    void releaseRequest_regulator_task_without_assignee() {
        final String operatorAssignee = "operatorAssignee";
        final String regulatorAssignee = "regulatorAssignee";
        Request request = Request.builder()
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .operatorAssignee(operatorAssignee)
                .regulatorAssignee(regulatorAssignee)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW)
            .build();

        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
            .thenReturn(Optional.of(RoleTypeConstants.REGULATOR));

        service.releaseRequest(requestTask);
        assertEquals(operatorAssignee, request.getPayload().getOperatorAssignee());
        assertNull(request.getPayload().getRegulatorAssignee());

        verify(authorizationRulesQueryService, times(1))
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
    }

    @Test
    void releaseRequest_regulator_request_regulator_assignee_other_than_task_assignee() {
        final String regulatorAssignee = "regulatorAssignee";
        Request request = Request.builder()
            .payload(InstallationAccountOpeningRequestPayload.builder().regulatorAssignee(regulatorAssignee).build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW)
            .assignee("assignee")
            .build();

        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
            .thenReturn(Optional.of(RoleTypeConstants.REGULATOR));

        service.releaseRequest(requestTask);

        verify(authorizationRulesQueryService, times(1))
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
    }

    @Test
    void releaseRequest_regulator_request_without_regulator_assignee() {
        final String operatorAssignee = "operatorAssignee";
        Request request = Request.builder()
            .payload(InstallationAccountOpeningRequestPayload.builder().operatorAssignee(operatorAssignee).build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW)
            .assignee("assignee")
            .build();

        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
            .thenReturn(Optional.of(RoleTypeConstants.REGULATOR));

        service.releaseRequest(requestTask);

        verify(authorizationRulesQueryService, times(1))
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
    }

    @Test
    void releaseRequest_null_role_type() {
        final String operatorAssignee = "operatorAssignee";
        final String regulatorAssignee = "regulatorAssignee";
        Request request = Request.builder()
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .operatorAssignee(operatorAssignee)
                .regulatorAssignee(regulatorAssignee)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
            .assignee(operatorAssignee)
            .build();

        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
            .thenReturn(Optional.empty());

        service.releaseRequest(requestTask);

        verify(authorizationRulesQueryService, times(1))
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
    }

    @Test
    void releaseRequest_verifier_task() {
        final String operatorAssignee = "operatorAssignee";
        final String regulatorAssignee = "regulatorAssignee";
        Request request = Request.builder()
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .operatorAssignee(operatorAssignee)
                .regulatorAssignee(regulatorAssignee)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW)
            .assignee(regulatorAssignee)
            .build();

        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
            .thenReturn(Optional.of(RoleTypeConstants.VERIFIER));

        service.releaseRequest(requestTask);

        verify(authorizationRulesQueryService, times(1))
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
    }

    @Test
    void releaseRequest_peer_review_task() {
        Request request = Request.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW)
            .build();

        service.releaseRequest(requestTask);
    }
}
