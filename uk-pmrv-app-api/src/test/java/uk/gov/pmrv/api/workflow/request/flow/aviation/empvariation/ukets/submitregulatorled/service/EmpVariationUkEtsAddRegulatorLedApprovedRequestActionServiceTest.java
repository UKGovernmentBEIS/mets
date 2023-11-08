package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.mapper.EmpVariationUkEtsSubmitRegulatorLedMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsAddRegulatorLedApprovedRequestActionServiceTest {

	@InjectMocks
    private EmpVariationUkEtsAddRegulatorLedApprovedRequestActionService cut;
	
	@Mock
	private RequestService requestService;
	
	@Mock
	private RequestActionUserInfoResolver requestActionUserInfoResolver;
	
	@Mock
	private RequestAviationAccountQueryService requestAviationAccountQueryService;
	
	@Test
	void add() {
		String requestId = "RequestId";
		Long accountId = 1L;
		
		DecisionNotification decisionNotification = DecisionNotification.builder()
			.operators(Set.of("op1"))
			.signatory("sign")
			.build();
		
		EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
				.regulatorReviewer("reviewer")
				.decisionNotification(decisionNotification)
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
						.operatorDetails(EmpOperatorDetails.builder()
								.crcoCode("crco")
								.build())
						.abbreviations(EmpAbbreviations.builder()
								.exist(true)
								.build())
						.build())
				.build();
		
		Request request = Request.builder()
				.accountId(accountId)
				.payload(requestPayload)
				.build();
		
		RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
				.crcoCode("CrcoCode")
				.build();
		
		Map<String, RequestActionUserInfo> usersInfo = Map.of(
				"user1", RequestActionUserInfo.builder().name("username1").build()
				);
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		when(requestActionUserInfoResolver.getUsersInfo(decisionNotification.getOperators(),
				decisionNotification.getSignatory(), request)).thenReturn(usersInfo);
		
		cut.add(requestId);
		
		verify(requestService, times(1)).findRequestById(requestId);
		verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
		verify(requestActionUserInfoResolver, times(1)).getUsersInfo(decisionNotification.getOperators(),
				decisionNotification.getSignatory(), request);
		verify(requestService, times(1)).addActionToRequest(request,
				Mappers.getMapper(EmpVariationUkEtsSubmitRegulatorLedMapper.class)
						.toEmpVariationApplicationRegulatorLedApprovedRequestActionPayload(requestPayload, accountInfo,
								usersInfo),
				RequestActionType.EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED, "reviewer");
		
	}
}
