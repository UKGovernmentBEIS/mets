package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.mapper.InstallationInspectionMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public abstract class InstallationInspectionAddSubmittedRequestActionService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private static final InstallationInspectionMapper INSTALLATION_INSPECTION_MAPPER = Mappers.getMapper(InstallationInspectionMapper.class);


    @Transactional
    public void add(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final InstallationInspectionRequestPayload requestPayload =
                (InstallationInspectionRequestPayload) request.getPayload();

        final InstallationInspectionApplicationSubmittedRequestActionPayload actionPayload = INSTALLATION_INSPECTION_MAPPER.toSubmittedActionPayload(requestPayload);

        final DecisionNotification notification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
                .getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
        actionPayload.setUsersInfo(usersInfo);

        requestService.addActionToRequest(request,
                actionPayload,
                getRequestActionType(),
                requestPayload.getRegulatorAssignee());
    }


    protected abstract  RequestActionType getRequestActionType();
}
