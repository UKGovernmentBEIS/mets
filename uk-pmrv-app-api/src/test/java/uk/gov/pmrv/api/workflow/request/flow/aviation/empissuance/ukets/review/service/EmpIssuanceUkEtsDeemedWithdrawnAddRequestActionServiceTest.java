package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsDeemedWithdrawnAddRequestActionServiceTest {

    @InjectMocks
    private EmpIssuanceUkEtsDeemedWithdrawnAddRequestActionService addRequestActionService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void addRequestAction() {
        String requestId = "1L";
        Set<String> operatorsNotified = Set.of("operatorUser");
        String regulatorUser = "regulatorUser";
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(operatorsNotified)
            .signatory(regulatorUser)
            .build();
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder().type(EmpIssuanceDeterminationType.APPROVED).build();
        EmpIssuanceUkEtsRequestPayload requestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
            .decisionNotification(decisionNotification)
            .determination(determination)
            .regulatorReviewer(regulatorUser)
            .build();
        Request request = Request.builder().id(requestId).payload(requestPayload).build();
        Map<String, RequestActionUserInfo> usersInfo = Map.of(
            "operatorUser", RequestActionUserInfo.builder().name("operatorUserName").build(),
            "regulatorUser", RequestActionUserInfo.builder().name("regulatorUserName").build()
        );

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(operatorsNotified, regulatorUser, request)).thenReturn(usersInfo);

        //invoke
        addRequestActionService.addRequestAction(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1)).getUsersInfo(operatorsNotified, regulatorUser, request);

        EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload requestActionPayload = EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload.builder()
            .payloadType(RequestActionPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD)
            .decisionNotification(decisionNotification)
            .determination(determination)
            .usersInfo(usersInfo)
            .build();

        verify(requestService, times(1))
            .addActionToRequest(request, requestActionPayload,  RequestActionType.EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN, regulatorUser);
    }
}