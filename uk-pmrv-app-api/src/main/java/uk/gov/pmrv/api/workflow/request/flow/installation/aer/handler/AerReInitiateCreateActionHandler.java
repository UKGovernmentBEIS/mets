package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;

@Component
@RequiredArgsConstructor
public class AerReInitiateCreateActionHandler implements RequestCreateActionHandler<ReportRelatedRequestCreateActionPayload> {

    private final RequestService requestService;
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, RequestCreateActionType type, ReportRelatedRequestCreateActionPayload payload, PmrvUser pmrvUser) {
        Request request = requestService.findRequestById(payload.getRequestId());
        AerRequestPayload requestPayload = (AerRequestPayload) request.getPayload();
        requestPayload.clearRegulatorData();

        // Restart AER
        startProcessRequestService.reStartProcess(request);

        // Add action
        requestService.addActionToRequest(
                request,
                null,
                RequestActionType.AER_APPLICATION_RE_INITIATED,
                pmrvUser.getUserId());

        return payload.getRequestId();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.AER;
    }
}
