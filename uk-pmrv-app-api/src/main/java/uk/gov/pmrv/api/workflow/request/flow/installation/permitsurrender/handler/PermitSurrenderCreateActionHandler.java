package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;

@Component
@RequiredArgsConstructor
public class PermitSurrenderCreateActionHandler implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {
    
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, RequestCreateActionEmptyPayload payload,
            AppUser appUser) {
        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMIT_SURRENDER)
                .accountId(accountId)
                .requestPayload(PermitSurrenderRequestPayload.builder()
                        .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
                        .operatorAssignee(appUser.getUserId())
                        .build())
                .requestMetadata(PermitSurrenderRequestMetadata.builder()
                        .type(RequestMetadataType.PERMIT_SURRENDER)
                        .build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.PERMIT_SURRENDER;
    }

}
