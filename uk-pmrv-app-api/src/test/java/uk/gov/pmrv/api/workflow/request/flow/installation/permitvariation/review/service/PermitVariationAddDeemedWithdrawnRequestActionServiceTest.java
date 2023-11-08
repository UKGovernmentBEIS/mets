package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationDeemedWithdrawnDetermination;

@ExtendWith(MockitoExtension.class)
class PermitVariationAddDeemedWithdrawnRequestActionServiceTest {

	@InjectMocks
    private PermitVariationAddDeemedWithdrawnRequestActionService cut;

    @Mock
    private RequestService requestService;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void add() {
    	String requestId = "requestId";
    	Set<String> operators = Set.of("oper");
    	String signatory = "sign";
    	PermitVariationDeemedWithdrawnDetermination determination = PermitVariationDeemedWithdrawnDetermination.builder()
    			.type(DeterminationType.DEEMED_WITHDRAWN)
    			.reason("reason")
    			.build();
		DecisionNotification decisionNotification = DecisionNotification.builder()
				.signatory(signatory)
				.operators(operators)
				.build();
    	PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
				.regulatorReviewer("reg")
				.decisionNotification(decisionNotification)
				.determination(determination)
				.build();
		Request request = Request.builder()
				.id(requestId)
				.payload(requestPayload)
				.build();
		Map<String, RequestActionUserInfo> usersInfo = Map.of("oper", RequestActionUserInfo.builder().name("operator1").build());
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(operators, signatory, request))
            .thenReturn(usersInfo);
        
        PermitVariationApplicationDeemedWithdrawnRequestActionPayload expectedActionPayload = PermitVariationApplicationDeemedWithdrawnRequestActionPayload.builder()
        		.payloadType(RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD)
        		.decisionNotification(decisionNotification)
        		.determination(determination)
        		.usersInfo(usersInfo)
        		.build();
        
        cut.add(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1)).getUsersInfo(operators, signatory, request);
        verify(requestService, times(1)).addActionToRequest(request, expectedActionPayload, RequestActionType.PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN, requestPayload.getRegulatorReviewer());
    }
}
