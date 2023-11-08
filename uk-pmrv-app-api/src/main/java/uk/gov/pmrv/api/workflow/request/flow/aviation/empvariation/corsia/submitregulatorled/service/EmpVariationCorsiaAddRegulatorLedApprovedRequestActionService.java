package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.mapper.EmpVariationCorsiaSubmitRegulatorLedMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaAddRegulatorLedApprovedRequestActionService {

	private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	private static final EmpVariationCorsiaSubmitRegulatorLedMapper MAPPER = 
		Mappers.getMapper(EmpVariationCorsiaSubmitRegulatorLedMapper.class);
	
	@Transactional
	public void add(final String requestId) {
		
		final Request request = requestService.findRequestById(requestId);
		final EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
		
		final RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
		
		final DecisionNotification notification = requestPayload.getDecisionNotification();
		final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
				.getUsersInfo(notification.getOperators(), notification.getSignatory(), request);

		final EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload actionPayload = MAPPER
				.toEmpVariationApplicationRegulatorLedApprovedRequestActionPayload(requestPayload, accountInfo, usersInfo);

		requestService.addActionToRequest(request, 
				actionPayload, 
				RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED,
				requestPayload.getRegulatorReviewer());
	}
}
