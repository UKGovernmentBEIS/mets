package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesSubmittedService {

    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final RequestService requestService;
    private final WithholdingOfAllowancesOfficialNoticeService withholdingOfAllowancesOfficialNoticeService;

    public void submit(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final WithholdingOfAllowancesRequestPayload requestPayload =
            (WithholdingOfAllowancesRequestPayload) request.getPayload();

        final DecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        // generate official notice
        FileInfoDTO officialNotice =
            withholdingOfAllowancesOfficialNoticeService.generateWithholdingOfAllowancesOfficialNotice(request.getId());

        // set official notice to request payload
        requestPayload.setWithholdingOfAllowancesOfficialNotice(officialNotice);

        // create request action
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request);

        final WithholdingOfAllowancesApplicationSubmittedRequestActionPayload actionPayload =
            WithholdingOfAllowancesApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED_PAYLOAD)
                .withholdingOfAllowances(requestPayload.getWithholdingOfAllowances())
                .decisionNotification(decisionNotification)
                .usersInfo(usersInfo)
                .officialNotice(officialNotice)
                .build();
        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED,
            request.getPayload().getRegulatorAssignee());

        // send official notice
        withholdingOfAllowancesOfficialNoticeService.sendOfficialNotice(
            request,
            officialNotice,
            decisionNotification
        );
    }
}
