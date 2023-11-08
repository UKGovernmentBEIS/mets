package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service;

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

import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsDeemedWithdrawnAddRequestActionServiceTest {

	@InjectMocks
    private EmpVariationUkEtsDeemedWithdrawnAddRequestActionService addRequestActionService;

    @Mock
    private RequestService requestService;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void addRequestAction() {
        String requestId = "1L";
        Long accountId = 1L;
        Set<String> operatorsNotified = Set.of("operatorUser");
        String regulatorUser = "regulatorUser";
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(operatorsNotified)
            .signatory(regulatorUser)
            .build();
        EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder()
            .operatorDetails(EmpOperatorDetails.builder().build())
            .build();
        EmpVariationDetermination determination = EmpVariationDetermination.builder().type(EmpVariationDeterminationType.REJECTED).build();
        EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emp)
            .empVariationDetails(EmpVariationUkEtsDetails.builder().reason("test reason").build())
            .determination(determination)
            .decisionNotification(decisionNotification)
            .regulatorReviewer(regulatorUser)
            .build();
        Request request = Request.builder().id(requestId).accountId(accountId).payload(requestPayload).build();
        
        Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "operatorUser", RequestActionUserInfo.builder().name("operatorUserName").build(),
                "regulatorUser", RequestActionUserInfo.builder().name("regulatorUserName").build()
            );
        EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload requestActionPayload = EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD)
                .determination(determination)
                .decisionNotification(decisionNotification)
                .usersInfo(usersInfo)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(operatorsNotified, regulatorUser, request)).thenReturn(usersInfo);

        //invoke
        addRequestActionService.addRequestAction(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
            .addActionToRequest(request, requestActionPayload,  RequestActionType.EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN, regulatorUser);
    }
}
