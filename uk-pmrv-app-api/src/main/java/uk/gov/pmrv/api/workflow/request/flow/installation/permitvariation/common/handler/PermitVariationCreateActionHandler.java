package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitVariationCreateActionHandler implements RequestCreateActionHandler<RequestCreateActionEmptyPayload>{
    
    private final StartProcessRequestService startProcessRequestService;
	private final PermitQueryService permitQueryService;

    @Override
    public String process(Long accountId, RequestCreateActionType type, RequestCreateActionEmptyPayload payload,
            PmrvUser pmrvUser) {
    	final RoleType currentUserRoleType = pmrvUser.getRoleType();
		final PermitContainer originalPermitContainer = permitQueryService.getPermitContainerByAccountId(accountId);
    	
    	final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
	        .payloadType(RequestPayloadType.PERMIT_VARIATION_REQUEST_PAYLOAD)
			.originalPermitContainer(originalPermitContainer)
			.build();
    	
    	if(currentUserRoleType == RoleType.OPERATOR) {
    		requestPayload.setOperatorAssignee(pmrvUser.getUserId());
    	} else if (currentUserRoleType == RoleType.REGULATOR) {
    		requestPayload.setRegulatorAssignee(pmrvUser.getUserId());
    	} else {
    		throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, currentUserRoleType);
    	}
    	
        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMIT_VARIATION)
                .accountId(accountId)
                .requestPayload(requestPayload)
                .processVars(Map.of(
                		BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, currentUserRoleType.name()
                		))
				.requestMetadata(PermitVariationRequestMetadata.builder()
						.type(RequestMetadataType.PERMIT_VARIATION)
						.initiatorRoleType(currentUserRoleType)
						.build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.PERMIT_VARIATION;
    }

}