package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;


@Component
@RequiredArgsConstructor
public class AviationAccountClosureCreateActionHandler implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, 
                          RequestCreateActionEmptyPayload payload,
                          AppUser appUser) {
    	        
        RequestParams requestParams = createRequestParams(accountId, appUser);

        Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

	private RequestParams createRequestParams(Long accountId, AppUser appUser) {
		return RequestParams.builder()
            .type(RequestType.AVIATION_ACCOUNT_CLOSURE)
            .accountId(accountId)
            .requestPayload(AviationAccountClosureRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_ACCOUNT_CLOSURE_REQUEST_PAYLOAD)
                .regulatorAssignee(appUser.getUserId())
                .build())
            .build();
	}

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.AVIATION_ACCOUNT_CLOSURE;
    }

}
