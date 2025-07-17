package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermanentCessationSubmittedService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final PermanentCessationOfficialNoticeService officialNoticeService;

    public void submit(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermanentCessationRequestPayload requestPayload =
            (PermanentCessationRequestPayload) request.getPayload();

        final DecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        // generate official notice
        FileInfoDTO officialNotice =
                officialNoticeService.generatePermanentCessationOfficialNotice(requestId);

        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request);

        // create request action
        final PermanentCessationApplicationSubmittedRequestActionPayload actionPayload =
                PermanentCessationApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMANENT_CESSATION_APPLICATION_SUBMITTED_PAYLOAD)
                .permanentCessation(requestPayload.getPermanentCessation())
                .permanentCessationSectionsCompleted(requestPayload.getPermanentCessationSectionsCompleted())
                .permanentCessationAttachments(requestPayload.getPermanentCessationAttachments())
                .decisionNotification(decisionNotification)
                .officialNotice(officialNotice)
                .usersInfo(usersInfo)
                .build();

        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.PERMANENT_CESSATION_APPLICATION_SUBMITTED,
            request.getPayload().getRegulatorAssignee());

        // send official notice
        officialNoticeService.sendOfficialNotice(
                request,
                officialNotice,
                decisionNotification
        );
    }
}
