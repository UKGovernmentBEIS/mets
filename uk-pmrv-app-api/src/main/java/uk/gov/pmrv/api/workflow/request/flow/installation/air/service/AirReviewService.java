package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSaveReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.mapper.AirMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AirReviewService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private static final AirMapper AIR_MAPPER = Mappers.getMapper(AirMapper.class);

    @Transactional
    public void saveReview(final AirSaveReviewRequestTaskActionPayload payload,
                           final RequestTask requestTask) {

        final AirApplicationReviewRequestTaskPayload taskPayload =
            (AirApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setRegulatorReviewResponse(payload.getRegulatorReviewResponse());
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
    }

    @Transactional
    public void submitReview(final RequestTask requestTask,
                             final DecisionNotification notifyOperatorDecision,
                             final AppUser appUser) {

        final Request request = requestTask.getRequest();
        final AirRequestPayload requestPayload = (AirRequestPayload) request.getPayload();
        final AirApplicationReviewRequestTaskPayload taskPayload = (AirApplicationReviewRequestTaskPayload) requestTask.getPayload();

        // Update request
        requestPayload.setRegulatorReviewResponse(taskPayload.getRegulatorReviewResponse());
        requestPayload.setDecisionNotification(notifyOperatorDecision);
        requestPayload.setReviewAttachments(taskPayload.getReviewAttachments());
        requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        requestPayload.setRegulatorReviewer(appUser.getUserId());
    }

    @Transactional
    public void addReviewedRequestAction(final String requestId) {
        
        final Request request = requestService.findRequestById(requestId);
        final AirRequestPayload requestPayload = (AirRequestPayload) request.getPayload();
        final AirRequestMetadata requestMetadata = (AirRequestMetadata) request.getMetadata();

        final AirApplicationReviewedRequestActionPayload actionPayload = AIR_MAPPER
            .toAirApplicationReviewedRequestActionPayload(requestPayload, requestMetadata.getYear());

        final DecisionNotification notification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
            .getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
        actionPayload.setUsersInfo(usersInfo);

        requestService.addActionToRequest(
            request,
            actionPayload,
            RequestActionType.AIR_APPLICATION_REVIEWED,
            requestPayload.getRegulatorReviewer()
        );
    }
}
