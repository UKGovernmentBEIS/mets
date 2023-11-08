package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaApprovedAddRequestActionServiceTest {

    @InjectMocks
    private EmpIssuanceCorsiaApprovedAddRequestActionService addRequestActionService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Test
    void addRequestAction() {
        String requestId = "1L";
        Long accountId = 1L;
        Set<String> operatorsNotified = Set.of("operatorUser");
        String regulatorUser = "regulatorUser";
        EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia.builder()
            .operatorDetails(EmpCorsiaOperatorDetails.builder().build())
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(operatorsNotified)
            .signatory(regulatorUser)
            .build();
        EmpIssuanceDetermination
            determination = EmpIssuanceDetermination.builder().type(EmpIssuanceDeterminationType.APPROVED).build();
        EmpIssuanceCorsiaRequestPayload requestPayload = EmpIssuanceCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emp)
            .decisionNotification(decisionNotification)
            .determination(determination)
            .regulatorReviewer(regulatorUser)
            .build();
        Request request = Request.builder().id(requestId).accountId(accountId).payload(requestPayload).build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName("operatorName")
            .build();
        Map<String, RequestActionUserInfo> usersInfo = Map.of(
            "operatorUser", RequestActionUserInfo.builder().name("operatorUserName").build(),
            "regulatorUser", RequestActionUserInfo.builder().name("regulatorUserName").build()
        );

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(aviationAccountInfo);
        when(requestActionUserInfoResolver.getUsersInfo(operatorsNotified, regulatorUser, request)).thenReturn(usersInfo);

        //invoke
        addRequestActionService.addRequestAction(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(requestActionUserInfoResolver, times(1)).getUsersInfo(operatorsNotified, regulatorUser, request);

        EmpIssuanceCorsiaApplicationApprovedRequestActionPayload requestActionPayload = EmpIssuanceCorsiaApplicationApprovedRequestActionPayload.builder()
            .payloadType(RequestActionPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED_PAYLOAD)
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .operatorDetails(EmpCorsiaOperatorDetails.builder().build())
                .build())
            .decisionNotification(decisionNotification)
            .determination(determination)
            .usersInfo(usersInfo)
            .build();

        verify(requestService, times(1))
            .addActionToRequest(request, requestActionPayload,  RequestActionType.EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED, regulatorUser);
    }
}