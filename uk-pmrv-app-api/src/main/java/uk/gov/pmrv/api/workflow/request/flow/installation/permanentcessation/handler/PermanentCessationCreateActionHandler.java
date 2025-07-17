package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;

@Component
@RequiredArgsConstructor
public class PermanentCessationCreateActionHandler implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, RequestCreateActionEmptyPayload payload, AppUser appUser) {

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMANENT_CESSATION)
                .accountId(accountId)
                .requestPayload(PermanentCessationRequestPayload.builder()
                        .payloadType(RequestPayloadType.PERMANENT_CESSATION_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .permanentCessation(PermanentCessation.builder().build())
                        .build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();

    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.PERMANENT_CESSATION;
    }
}
