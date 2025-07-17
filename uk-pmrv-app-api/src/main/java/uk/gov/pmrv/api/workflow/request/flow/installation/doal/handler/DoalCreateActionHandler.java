package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

@Component
@RequiredArgsConstructor
public class DoalCreateActionHandler implements RequestAccountCreateActionHandler<DoalRequestCreateActionPayload> {

    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, DoalRequestCreateActionPayload payload,
                          AppUser appUser) {

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.DOAL)
                .accountId(accountId)
                .requestPayload(DoalRequestPayload.builder()
                        .payloadType(RequestPayloadType.DOAL_REQUEST_PAYLOAD)
                        .reportingYear(payload.getYear())
                        .regulatorAssignee(appUser.getUserId())
                        .build())
                .requestMetadata(DoalRequestMetadata.builder()
                        .type(RequestMetadataType.DOAL)
                        .year(payload.getYear())
                        .build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.DOAL;
    }
}
