package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesInitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesReCreateActionPayload;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WithholdingOfAllowancesReInitiateCreateActionHandler implements RequestAccountCreateActionHandler<WithholdingOfAllowancesReCreateActionPayload> {

    private final RequestService requestService;
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, WithholdingOfAllowancesReCreateActionPayload payload, AppUser appUser) {

        Request request = requestService.findRequestById(payload.getRequestId());

        startProcessRequestService.reStartProcess(request, Map.of(BpmnProcessConstants.WITHHOLDING_OF_ALLOWANCES_INITIATION_TYPE, WithholdingOfAllowancesInitiationType.RE_INITIATED));

        requestService.addActionToRequest(
                request,
                null,
                RequestActionType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_RE_INITIATED,
                appUser.getUserId());

        return payload.getRequestId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.WITHHOLDING_OF_ALLOWANCES_RE_INITIATE;
    }
}
