package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRInitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BDRReInitiateCreateActionHandler implements RequestAccountCreateActionHandler<ReportRelatedRequestCreateActionPayload> {

    private final RequestService requestService;
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, ReportRelatedRequestCreateActionPayload payload, AppUser appUser) {
        Request request = requestService.findRequestById(payload.getRequestId());
        BDRRequestMetadata requestMetadata = (BDRRequestMetadata) request.getMetadata();

        requestMetadata.setBdrInitiationType(BDRInitiationType.RE_INITIATED);

        startProcessRequestService.reStartProcess(request, Map.of(BpmnProcessConstants.BDR_INITIATION_TYPE, BDRInitiationType.RE_INITIATED));

        requestService.addActionToRequest(
                request,
                null,
                RequestActionType.BDR_APPLICATION_RE_INITIATED,
                appUser.getUserId());

        return payload.getRequestId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.BDR;
    }
}
