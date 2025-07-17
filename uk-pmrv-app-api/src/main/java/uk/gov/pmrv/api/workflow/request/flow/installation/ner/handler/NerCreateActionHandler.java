package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

@Component
@RequiredArgsConstructor
public class NerCreateActionHandler implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(final Long accountId,
                          final RequestCreateActionEmptyPayload payload,
                          final AppUser appUser) {

        final RequestParams requestParams = RequestParams.builder()
            .accountId(accountId)
            .type(RequestType.NER)
            .requestPayload(NerRequestPayload.builder()
				.payloadType(RequestPayloadType.NER_REQUEST_PAYLOAD)
				.operatorAssignee(appUser.getUserId())
				.build())
            .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.NER;
    }

}
