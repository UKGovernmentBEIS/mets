package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2InitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BDRS2ReInitiateCreateActionHandler implements RequestAccountCreateActionHandler<ReportRelatedRequestCreateActionPayload> {

    private final RequestService requestService;
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, ReportRelatedRequestCreateActionPayload payload, AppUser appUser) {
        Request request = requestService.findRequestById(payload.getRequestId());
        BDRS2RequestMetadata requestMetadata = (BDRS2RequestMetadata) request.getMetadata();

        requestMetadata.setBdrs2InitiationType(BDRS2InitiationType.RE_INITIATED);

        startProcessRequestService.reStartProcess(request, Map.of(BpmnProcessConstants.BDRS2_INITIATION_TYPE, BDRS2InitiationType.RE_INITIATED));

        requestService.addActionToRequest(
                request,
                null,
                RequestActionType.BDRS2_APPLICATION_RE_INITIATED,
                appUser.getUserId());

        return payload.getRequestId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.BDRS2;
    }
}
