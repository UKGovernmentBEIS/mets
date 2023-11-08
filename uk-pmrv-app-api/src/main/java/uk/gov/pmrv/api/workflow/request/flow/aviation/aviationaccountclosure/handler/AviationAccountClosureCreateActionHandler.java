package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;


@Component
@RequiredArgsConstructor
public class AviationAccountClosureCreateActionHandler implements RequestCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, 
                          RequestCreateActionType type, 
                          RequestCreateActionEmptyPayload payload,
                          PmrvUser pmrvUser) {
    	        
        RequestParams requestParams = createRequestParams(accountId, pmrvUser);

        Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

	private RequestParams createRequestParams(Long accountId, PmrvUser pmrvUser) {
		return RequestParams.builder()
            .type(RequestType.AVIATION_ACCOUNT_CLOSURE)
            .accountId(accountId)
            .requestPayload(AviationAccountClosureRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_ACCOUNT_CLOSURE_REQUEST_PAYLOAD)
                .regulatorAssignee(pmrvUser.getUserId())
                .build())
            .build();
	}

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.AVIATION_ACCOUNT_CLOSURE;
    }

}
