package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesClosedService {

    private final RequestService requestService;

    public void close(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final WithholdingOfAllowancesRequestPayload requestPayload =
            (WithholdingOfAllowancesRequestPayload) request.getPayload();

        // create request action
        final WithholdingOfAllowancesApplicationClosedRequestActionPayload actionPayload =
            WithholdingOfAllowancesApplicationClosedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_CLOSED_PAYLOAD)
                .closeJustification(requestPayload.getCloseJustification())
                .withholdingOfAllowancesWithdrawalAttachments(requestPayload.getWithholdingOfAllowancesWithdrawalAttachments())
                .build();
        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_CLOSED,
            request.getPayload().getRegulatorAssignee());
    }
}
