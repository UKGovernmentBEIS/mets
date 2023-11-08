package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service;

import java.util.Map;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.mapper.EmpVariationUkEtsSubmitRegulatorLedMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsAddRegulatorLedApprovedRequestActionService {

	private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	private static final EmpVariationUkEtsSubmitRegulatorLedMapper MAPPER = Mappers
			.getMapper(EmpVariationUkEtsSubmitRegulatorLedMapper.class);
	
	@Transactional
	public void add(final String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
		
		final RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
		
		final DecisionNotification notification = requestPayload.getDecisionNotification();
		final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
				.getUsersInfo(notification.getOperators(), notification.getSignatory(), request);

		final EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload actionPayload = MAPPER
				.toEmpVariationApplicationRegulatorLedApprovedRequestActionPayload(requestPayload, accountInfo, usersInfo);

		requestService.addActionToRequest(request, 
				actionPayload, 
				RequestActionType.EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED,
				requestPayload.getRegulatorReviewer());
	}
}
