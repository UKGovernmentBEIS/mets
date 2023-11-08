package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationDeterminedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

@Service
@RequiredArgsConstructor
public class PermitTransferADeterminedService {

    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    public void deemWithdrawn(final String requestId) {
        this.addAction(
            requestId, 
            RequestActionType.PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN, 
            RequestActionPayloadType.PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD
        );
    }

    public void grant(final String requestId) {
        this.addAction(
            requestId, 
            RequestActionType.PERMIT_TRANSFER_A_APPLICATION_GRANTED, 
            RequestActionPayloadType.PERMIT_TRANSFER_A_APPLICATION_GRANTED_PAYLOAD
        );
    }

    public void reject(final String requestId) {
        this.addAction(
            requestId, 
            RequestActionType.PERMIT_TRANSFER_A_APPLICATION_REJECTED, 
            RequestActionPayloadType.PERMIT_TRANSFER_A_APPLICATION_REJECTED_PAYLOAD
        );
    }

    private void addAction(final String requestId, 
                           final RequestActionType actionType,
                           final RequestActionPayloadType actionPayloadType) {

        final Request request = requestService.findRequestById(requestId);
        final PermitTransferARequestPayload requestPayload = (PermitTransferARequestPayload) request.getPayload();
        
        final Pair<DecisionNotification, String> usersIds = this.resolveUsersIds(request);
        final DecisionNotification decisionNotification = usersIds.getLeft();
        final String reviewer = usersIds.getRight();
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver.getUsersInfo(
            decisionNotification.getOperators(), decisionNotification.getSignatory(), request
        );
        
        final FileInfoDTO officialNotice = requestPayload.getOfficialNotice();

        final PermitTransferAApplicationDeterminedRequestActionPayload actionPayload =
            PermitTransferAApplicationDeterminedRequestActionPayload.builder()
                .payloadType(actionPayloadType)
                .decisionNotification(decisionNotification)
                .officialNotice(officialNotice)
                .usersInfo(usersInfo)
                .build();

        requestService.addActionToRequest(request,
            actionPayload,
            actionType,
            reviewer);
    }

    private Pair<DecisionNotification, String> resolveUsersIds(final Request request) {

        final PermitTransferARequestPayload requestPayload = (PermitTransferARequestPayload) request.getPayload();
        final String relatedRequestId = requestPayload.getRelatedRequestId();
        final Request relatedRequest = requestService.findRequestById(relatedRequestId);
        final PermitTransferBRequestPayload relatedRequestPayload =
            (PermitTransferBRequestPayload) relatedRequest.getPayload();
        final String signatory = relatedRequestPayload.getDecisionNotification().getSignatory();
        final String primaryContact =
            requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .map(UserInfoDTO::getUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final String serviceContact =
            requestAccountContactQueryService.getRequestAccountContact(request, AccountContactType.SERVICE)
                .map(UserInfoDTO::getUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));
        final Set<String> operators =
            serviceContact == null || serviceContact.equals(primaryContact) ? 
                Set.of(primaryContact) : 
                Set.of(primaryContact, serviceContact);
        final String reviewerName = relatedRequestPayload.getRegulatorReviewer();
        final DecisionNotification notification = DecisionNotification.builder()
            .operators(operators)
            .signatory(signatory)
            .build();

        return Pair.of(notification, reviewerName);
    }
}
