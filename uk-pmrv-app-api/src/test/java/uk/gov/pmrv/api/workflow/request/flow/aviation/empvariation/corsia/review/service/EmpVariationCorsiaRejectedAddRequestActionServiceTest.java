package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaRejectedAddRequestActionServiceTest {

	@InjectMocks
    private EmpVariationCorsiaRejectedAddRequestActionService addRequestActionService;

    @Mock
    private RequestService requestService;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void addRequestAction() {
        String requestId = "1L";
        Long accountId = 1L;
        String regulatorUser = "regulatorUser";
        Set<String> operatorsNotified = Set.of("operatorUser");
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(operatorsNotified)
                .signatory(regulatorUser)
                .build();
        EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia.builder()
            .operatorDetails(EmpCorsiaOperatorDetails.builder().build())
            .build();
        EmpVariationDetermination determination = EmpVariationDetermination.builder().type(EmpVariationDeterminationType.REJECTED).build();
        EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emp)
            .empVariationDetails(EmpVariationCorsiaDetails.builder().reason("test reason").build())
            .determination(determination)
            .decisionNotification(decisionNotification)
            .regulatorReviewer(regulatorUser)
            .build();
        Request request = Request.builder().id(requestId).accountId(accountId).payload(requestPayload).build();
        Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "operatorUser", RequestActionUserInfo.builder().name("operatorUserName").build(),
                "regulatorUser", RequestActionUserInfo.builder().name("regulatorUserName").build()
            );

        EmpVariationCorsiaApplicationRejectedRequestActionPayload requestActionPayload = EmpVariationCorsiaApplicationRejectedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REJECTED_PAYLOAD)
                .determination(determination)
                .decisionNotification(decisionNotification)
                .usersInfo(usersInfo)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(operatorsNotified, regulatorUser, request)).thenReturn(usersInfo);
        
        //invoke
        addRequestActionService.addRequestAction(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
        		request, requestActionPayload,  RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_REJECTED, regulatorUser);
    }
}
