package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;

import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermanentCessationSubmittedServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Mock
    private PermanentCessationOfficialNoticeService officialNoticeService;

    @InjectMocks
    private PermanentCessationSubmittedService permanentCessationSubmittedService;

    @Mock
    private Request request;

    @Mock
    private PermanentCessationRequestPayload requestPayload;

    @Mock
    private DecisionNotification decisionNotification;

    private static final String REQUEST_ID = "test-request-id";

    @BeforeEach
    void setUp() {
        when(requestService.findRequestById(REQUEST_ID)).thenReturn(request);
        when(request.getPayload()).thenReturn(requestPayload);
        when(requestPayload.getDecisionNotification()).thenReturn(decisionNotification);
        when(decisionNotification.getOperators()).thenReturn(Set.of("operator1", "Operator User"));
        when(decisionNotification.getSignatory()).thenReturn("signatory-user");
    }

    @Test
    void submit_shouldCreateRequestActionAndInvokeServiceMethods() {
        // Arrange
        Map<String, RequestActionUserInfo> usersInfo = Map.of("operator1", mock(RequestActionUserInfo.class));
        when(requestActionUserInfoResolver.getUsersInfo(any(), any(), any())).thenReturn(usersInfo);
        when(requestPayload.getRegulatorAssignee()).thenReturn("regulator-assignee");
        when(requestPayload.getPermanentCessation()).thenReturn(PermanentCessation.builder().build());

        // Act
        permanentCessationSubmittedService.submit(REQUEST_ID);

        // Assert
        verify(requestService, times(1)).findRequestById(REQUEST_ID);
        verify(requestActionUserInfoResolver, times(1)).getUsersInfo(any(), any(), any());
        verify(requestService, times(1)).addActionToRequest(eq(request), any(PermanentCessationApplicationSubmittedRequestActionPayload.class),
                eq(RequestActionType.PERMANENT_CESSATION_APPLICATION_SUBMITTED), eq("regulator-assignee"));
    }
}