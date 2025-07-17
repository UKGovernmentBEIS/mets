package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.mapper.VirMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VirReviewService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private static final VirMapper virMapper = Mappers.getMapper(VirMapper.class);

    @Transactional
    public void saveReview(final VirSaveReviewRequestTaskActionPayload payload,
                           final RequestTask requestTask) {
        final VirApplicationReviewRequestTaskPayload taskPayload = (VirApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setRegulatorReviewResponse(payload.getRegulatorReviewResponse());
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
    }

    @Transactional
    public void submitReview(RequestTask requestTask, DecisionNotification notifyOperatorDecision, AppUser appUser) {
        Request request = requestTask.getRequest();
        VirRequestPayload virRequestPayload = (VirRequestPayload) request.getPayload();
        final VirApplicationReviewRequestTaskPayload taskPayload = (VirApplicationReviewRequestTaskPayload) requestTask.getPayload();

        // Update request
        virRequestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        virRequestPayload.setRegulatorReviewResponse(taskPayload.getRegulatorReviewResponse());
        virRequestPayload.setDecisionNotification(notifyOperatorDecision);
        virRequestPayload.setRegulatorReviewer(appUser.getUserId());
    }

    @Transactional
    public void addReviewedRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final VirRequestPayload requestPayload = (VirRequestPayload) request.getPayload();
        final VirRequestMetadata virRequestMetadata = (VirRequestMetadata) request.getMetadata();

        VirApplicationReviewedRequestActionPayload actionPayload = virMapper
                .toVirApplicationReviewedRequestActionPayload(requestPayload, virRequestMetadata.getYear());

        final DecisionNotification notification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
                .getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
        actionPayload.setUsersInfo(usersInfo);

        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.VIR_APPLICATION_REVIEWED,
                requestPayload.getRegulatorReviewer());
    }
}
