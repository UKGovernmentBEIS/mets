package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;

@ExtendWith(MockitoExtension.class)
class PermitVariationAddRegulatorLedApprovedRequestActionServiceTest {

	@InjectMocks
    private PermitVariationAddRegulatorLedApprovedRequestActionService cut;

    @Mock
    private RequestService requestService;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;
    
    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void add() {
    	String requestId = "requestId";
    	Long accountId = 1L;
    	LocalDate activationDate = LocalDate.now().plusDays(10);
    	Set<String> operators = Set.of("oper");
    	String signatory = "sign";
    	PermitVariationRegulatorLedGrantDetermination determination = PermitVariationRegulatorLedGrantDetermination.builder()
    			.reason("reason")
    			.activationDate(activationDate)
    			.build();
		DecisionNotification decisionNotification = DecisionNotification.builder()
				.signatory(signatory)
				.operators(operators)
				.build();
    	PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
				.regulatorReviewer("reg")
				.decisionNotification(decisionNotification)
				.determinationRegulatorLed(determination)
				.build();
		Request request = Request.builder()
				.id(requestId)
				.payload(requestPayload)
				.accountId(accountId)
				.build();
		InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
				.installationName("acc1")
				.companyReferenceNumber("refNumber")
				.build();
		Map<String, RequestActionUserInfo> usersInfo = Map.of("oper", RequestActionUserInfo.builder().name("operator1").build());
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);
        when(requestActionUserInfoResolver.getUsersInfo(operators, signatory, request))
            .thenReturn(usersInfo);
        
        PermitVariationApplicationRegulatorLedApprovedRequestActionPayload expectedActionPayload = PermitVariationApplicationRegulatorLedApprovedRequestActionPayload.builder()
        		.payloadType(RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD)
        		.decisionNotification(decisionNotification)
        		.determination(determination)
        		.usersInfo(usersInfo)
        		.installationOperatorDetails(installationOperatorDetails)
        		.build();
        
        cut.add(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1)).getUsersInfo(operators, signatory, request);
        verify(requestService, times(1)).addActionToRequest(request, expectedActionPayload, RequestActionType.PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED, requestPayload.getRegulatorReviewer());
    }
}
