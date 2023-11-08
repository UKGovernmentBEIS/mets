package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReturnOfAllowancesSubmittedService {

    private final RequestService requestService;
    private final ReturnOfAllowancesOfficialNoticeService returnOfAllowancesOfficialNoticeService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    public void submit(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final ReturnOfAllowancesRequestPayload requestPayload =
            (ReturnOfAllowancesRequestPayload) request.getPayload();

        final DecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        // generate official notice
        FileInfoDTO officialNotice =
            returnOfAllowancesOfficialNoticeService.generateReturnOfAllowancesOfficialNotice(requestId);

        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request);

        // create request action
        final ReturnOfAllowancesApplicationSubmittedRequestActionPayload actionPayload =
            ReturnOfAllowancesApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED_PAYLOAD)
                .returnOfAllowances(requestPayload.getReturnOfAllowances())
                .decisionNotification(decisionNotification)
                .officialNotice(officialNotice)
                .usersInfo(usersInfo)
                .build();
        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED,
            request.getPayload().getRegulatorAssignee());

        // send official notice
        returnOfAllowancesOfficialNoticeService.sendOfficialNotice(
            request,
            officialNotice,
            decisionNotification
        );
    }
}
