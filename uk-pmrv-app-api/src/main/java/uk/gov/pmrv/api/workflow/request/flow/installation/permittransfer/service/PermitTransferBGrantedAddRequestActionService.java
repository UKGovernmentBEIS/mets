package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper.PermitTransferMapper;

@Service
@RequiredArgsConstructor
public class PermitTransferBGrantedAddRequestActionService {
    
    private static final PermitTransferMapper PERMIT_TRANSFER_MAPPER = Mappers.getMapper(PermitTransferMapper.class);
    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    
    public void addRequestAction(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();
        
        final InstallationOperatorDetails installationOperatorDetails =
            installationOperatorDetailsQueryService.getInstallationOperatorDetails(
                request.getAccountId());

        final PermitTransferBApplicationGrantedRequestActionPayload actionPayload =
            PERMIT_TRANSFER_MAPPER.toPermitTransferBApplicationGrantedRequestActionPayload(
                requestPayload
            );

        // get users' information
        final DecisionNotification notification =
            ((PermitIssuanceRequestPayload) request.getPayload()).getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo =
            requestActionUserInfoResolver.getUsersInfo(notification.getOperators(), notification.getSignatory(),
                request);
        actionPayload.setUsersInfo(usersInfo);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);

        final String reviewer = requestPayload.getRegulatorReviewer();

        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.PERMIT_TRANSFER_B_APPLICATION_GRANTED,
            reviewer);
    }
}
