package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.mapper.AviationVirMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@Service
@RequiredArgsConstructor
public class AviationVirReviewService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private static final AviationVirMapper VIR_MAPPER = Mappers.getMapper(AviationVirMapper.class);

    @Transactional
    public void saveReview(final AviationVirSaveReviewRequestTaskActionPayload payload,
                           final RequestTask requestTask) {
        
        final AviationVirApplicationReviewRequestTaskPayload taskPayload =
            (AviationVirApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setRegulatorReviewResponse(payload.getRegulatorReviewResponse());
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
    }

    @Transactional
    public void submitReview(final RequestTask requestTask,
                             final DecisionNotification notifyOperatorDecision,
                             final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final AviationVirRequestPayload virRequestPayload = (AviationVirRequestPayload) request.getPayload();
        final AviationVirApplicationReviewRequestTaskPayload
            taskPayload = (AviationVirApplicationReviewRequestTaskPayload) requestTask.getPayload();

        // Update request
        virRequestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        virRequestPayload.setRegulatorReviewResponse(taskPayload.getRegulatorReviewResponse());
        virRequestPayload.setDecisionNotification(notifyOperatorDecision);
        virRequestPayload.setRegulatorReviewer(pmrvUser.getUserId());
    }

    @Transactional
    public void addReviewedRequestAction(final String requestId) {
        
        final Request request = requestService.findRequestById(requestId);
        final AviationVirRequestPayload requestPayload = (AviationVirRequestPayload) request.getPayload();
        final AviationVirRequestMetadata virRequestMetadata = (AviationVirRequestMetadata) request.getMetadata();

        final AviationVirApplicationReviewedRequestActionPayload actionPayload = VIR_MAPPER
                .toVirApplicationReviewedRequestActionPayload(requestPayload, virRequestMetadata.getYear());

        final DecisionNotification notification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
                .getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
        actionPayload.setUsersInfo(usersInfo);

        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.AVIATION_VIR_APPLICATION_REVIEWED,
                requestPayload.getRegulatorReviewer());
    }
}
