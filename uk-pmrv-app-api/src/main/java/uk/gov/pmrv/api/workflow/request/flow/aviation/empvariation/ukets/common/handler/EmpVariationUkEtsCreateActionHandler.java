package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmpVariationUkEtsCreateActionHandler implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {

	private final StartProcessRequestService startProcessRequestService;
	private final EmissionsMonitoringPlanQueryService empQueryService;

    @Override
    public String process(Long accountId, RequestCreateActionEmptyPayload payload,
            AppUser appUser) {
    	final String currentUserRoleType = appUser.getRoleType();
    	final EmissionsMonitoringPlanUkEtsContainer empContainer = 
    			empQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)
    			.map(EmissionsMonitoringPlanUkEtsDTO::getEmpContainer)
    			.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    	
    	final EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
	        .payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
	        .originalEmpContainer(empContainer)
	        .build();
    	
    	if(RoleTypeConstants.OPERATOR.equals(currentUserRoleType)) {
    		requestPayload.setOperatorAssignee(appUser.getUserId());
    	} else if (RoleTypeConstants.REGULATOR.equals(currentUserRoleType)) {
    		requestPayload.setRegulatorAssignee(appUser.getUserId());
    	} else {
    		throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, currentUserRoleType);
    	}
    	
        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.EMP_VARIATION_UKETS)
                .accountId(accountId)
                .requestPayload(requestPayload)
                .processVars(Map.of(
                		BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, currentUserRoleType
                		))
                .requestMetadata(EmpVariationRequestMetadata.builder()
						.type(RequestMetadataType.EMP_VARIATION)
						.initiatorRoleType(currentUserRoleType)
						.build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.EMP_VARIATION_UKETS;
    }
}
