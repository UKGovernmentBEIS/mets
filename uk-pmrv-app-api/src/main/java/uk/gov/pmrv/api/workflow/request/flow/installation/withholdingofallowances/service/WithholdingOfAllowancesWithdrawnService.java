package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesWithdrawnService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final WithholdingOfAllowancesOfficialNoticeService withholdingOfAllowancesOfficialNoticeService;

    public void withdraw(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final WithholdingOfAllowancesRequestPayload requestPayload =
            (WithholdingOfAllowancesRequestPayload) request.getPayload();

        final DecisionNotification decisionNotification = requestPayload.getWithdrawDecisionNotification();

        // generate official notice
        FileInfoDTO officialNotice =
            withholdingOfAllowancesOfficialNoticeService.generateWithholdingOfAllowancesWithdrawnOfficialNotice(request.getId());

        // set official notice to request payload
        requestPayload.setWithholdingOfAllowancesWithdrawnOfficialNotice(officialNotice);

        // create request action
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request);
        final WithholdingOfAllowancesApplicationWithdrawnRequestActionPayload actionPayload =
            WithholdingOfAllowancesApplicationWithdrawnRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_WITHDRAWN_PAYLOAD)
                .withholdingWithdrawal(requestPayload.getWithholdingWithdrawal())
                .decisionNotification(decisionNotification)
                .officialNotice(officialNotice)
                .usersInfo(usersInfo)
                .build();
        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_WITHDRAWN,
            request.getPayload().getRegulatorAssignee());

        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);

        // send official notice
        withholdingOfAllowancesOfficialNoticeService.sendOfficialNotice(
            request,
            officialNotice,
            decisionNotification
        );
    }
}
