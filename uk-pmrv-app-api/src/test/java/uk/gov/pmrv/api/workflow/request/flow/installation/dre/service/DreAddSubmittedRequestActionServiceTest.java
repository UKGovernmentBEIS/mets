package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

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

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

@ExtendWith(MockitoExtension.class)
class DreAddSubmittedRequestActionServiceTest {

	@InjectMocks
    private DreAddSubmittedRequestActionService cut;

    @Mock
    private RequestService requestService;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;
    
    @Test
    void add() {
    	String requestId = "1";
    	Set<String> operators = Set.of("oper");
    	String signatory = "sign";
    	DecisionNotification decisionNotification = DecisionNotification.builder()
				.signatory(signatory)
				.operators(operators)
				.build();
    	
    	Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.operatorAskedToResubmit(true)
						.regulatorComments("reg comments")
						.build())
				.build();
    	
    	final Request request = Request.builder()
                .id(requestId)
                .payload(DreRequestPayload.builder()
                		.decisionNotification(decisionNotification)
                		.regulatorAssignee("regulator")
                		.dre(dre)
                		.build())
                .build();
    	
    	Map<String, RequestActionUserInfo> usersInfo = Map.of("oper", RequestActionUserInfo.builder().name("operator1").build());

    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	when(requestActionUserInfoResolver.getUsersInfo(operators, signatory, request))
        	.thenReturn(usersInfo);
    	
    	DreApplicationSubmittedRequestActionPayload expectedActionPayload = DreApplicationSubmittedRequestActionPayload.builder()
        		.payloadType(RequestActionPayloadType.DRE_APPLICATION_SUBMITTED_PAYLOAD)
        		.decisionNotification(decisionNotification)
        		.usersInfo(usersInfo)
        		.dre(dre)
        		.build();
    	
    	cut.add(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1)).getUsersInfo(operators, signatory, request);
        verify(requestService, times(1)).addActionToRequest(request, expectedActionPayload, RequestActionType.DRE_APPLICATION_SUBMITTED, "regulator");
    }
}
