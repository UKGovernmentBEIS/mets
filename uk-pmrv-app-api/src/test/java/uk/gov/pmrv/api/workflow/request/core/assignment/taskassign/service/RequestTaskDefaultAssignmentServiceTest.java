package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.operator.OperatorRequestTaskDefaultAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.regulator.RegulatorRequestTaskDefaultAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestTaskDefaultAssignmentServiceTest {

    @InjectMocks
    private RequestTaskDefaultAssignmentService requestTaskDefaultAssignmentService;

    @Mock
    private OperatorRequestTaskDefaultAssignmentService operatorRequestTaskDefaultAssignmentService;

    @Mock
    private RegulatorRequestTaskDefaultAssignmentService regulatorRequestTaskDefaultAssignmentService;

    @BeforeEach
    void setup() {
        requestTaskDefaultAssignmentService = new RequestTaskDefaultAssignmentService(
            List.of(operatorRequestTaskDefaultAssignmentService, regulatorRequestTaskDefaultAssignmentService));
    }

    @Test
    void assignDefaultAssigneeToTask_operator() {
        RequestTask requestTask = RequestTask.builder().type(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE).build();

        when(operatorRequestTaskDefaultAssignmentService.getRoleType()).thenReturn(RoleTypeConstants.OPERATOR);

        requestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(RoleTypeConstants.OPERATOR, requestTask);

        verify(operatorRequestTaskDefaultAssignmentService, times(1)).assignDefaultAssigneeToTask(requestTask);
        verify(regulatorRequestTaskDefaultAssignmentService, never()).assignDefaultAssigneeToTask(any());
    }

    @Test
    void assignDefaultAssigneeToTask_regulator() {
        RequestTask requestTask = RequestTask.builder().type(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();

        when(operatorRequestTaskDefaultAssignmentService.getRoleType()).thenReturn(RoleTypeConstants.OPERATOR);
        when(regulatorRequestTaskDefaultAssignmentService.getRoleType()).thenReturn(RoleTypeConstants.REGULATOR);

        requestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(RoleTypeConstants.REGULATOR, requestTask);

        verify(regulatorRequestTaskDefaultAssignmentService, times(1)).assignDefaultAssigneeToTask(requestTask);
        verify(operatorRequestTaskDefaultAssignmentService, never()).assignDefaultAssigneeToTask(any());
    }

    @Test
    void assignDefaultAssigneeToTask_no_rule_for_role() {
        RequestTask requestTask = RequestTask.builder().type(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();

        when(operatorRequestTaskDefaultAssignmentService.getRoleType()).thenReturn(RoleTypeConstants.OPERATOR);
        when(regulatorRequestTaskDefaultAssignmentService.getRoleType()).thenReturn(RoleTypeConstants.REGULATOR);

        requestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(null, requestTask);

        verify(operatorRequestTaskDefaultAssignmentService, times(1)).getRoleType();
        verify(regulatorRequestTaskDefaultAssignmentService, times(1)).getRoleType();
        verifyNoMoreInteractions(operatorRequestTaskDefaultAssignmentService, regulatorRequestTaskDefaultAssignmentService);
    }
}