package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

@Component
@RequiredArgsConstructor
public class WithholdingOfAllowancesCreateActionHandler implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(final Long accountId, 
                          final RequestCreateActionEmptyPayload payload,
                          final AppUser appUser) {
        
        final RequestParams requestParams = RequestParams.builder()
            .type(RequestType.WITHHOLDING_OF_ALLOWANCES)
            .accountId(accountId)
            .requestPayload(WithholdingOfAllowancesRequestPayload.builder()
                .payloadType(RequestPayloadType.WITHHOLDING_OF_ALLOWANCES_REQUEST_PAYLOAD)
                .regulatorAssignee(appUser.getUserId())
                .build())
            .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.WITHHOLDING_OF_ALLOWANCES;
    }

}
