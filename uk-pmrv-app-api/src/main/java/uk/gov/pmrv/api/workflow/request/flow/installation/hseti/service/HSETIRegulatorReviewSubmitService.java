package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETI;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.mapper.HSETIMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation.HSETIValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationSaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIReviewGroup;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class HSETIRegulatorReviewSubmitService {

    private final HSETIValidatorService validationService;
    private final RequestService requestService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    private static final HSETIMapper HSETI_MAPPER = Mappers.getMapper(HSETIMapper.class);

    @Transactional
    public void submit(final RequestTask requestTask,
                        final DecisionNotification decisionNotification,
                        final AppUser appUser) {

        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
            (HSETIApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();


        validationService.validateRegulatorReview(taskPayload);


        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        Request request = requestTask.getRequest();
        HSETIRequestPayload requestPayload = (HSETIRequestPayload) request.getPayload();

        requestPayload.setDecisionNotification(decisionNotification);

        updateRequestPayload(requestTask, appUser);
    }

    @Transactional
    public void saveReviewGroupDecision(final HSETIApplicationSaveRegulatorReviewGroupDecisionRequestTaskActionPayload payload,
                                        final RequestTask requestTask) {

        final HSETIReviewGroup group = payload.getGroup();
        final HSETIRegulatorReviewDecision decision = payload.getDecision();

        final HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (HSETIApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        final Map<HSETIReviewGroup, HSETIRegulatorReviewDecision> reviewGroupDecisions = taskPayload.getRegulatorReviewGroupDecisions();

        reviewGroupDecisions.put(group, decision);

        final Map<String, Boolean> reviewSectionsCompleted = payload.getRegulatorReviewSectionsCompleted();
        taskPayload.setRegulatorReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void save(final HSETIApplicationRegulatorReviewSaveTaskActionPayload payload,
                     final RequestTask requestTask) {

        final HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (HSETIApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setOverallDecision(payload.getOverallDecision());

        final Map<String, Boolean> reviewSectionsCompleted = payload.getRegulatorReviewSectionsCompleted();
        taskPayload.setRegulatorReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void returnForAmends(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();

        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
            (HSETIApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        validationService.validateReturnForAmends(reviewRequestTaskPayload);

        updateRequestPayload(requestTask, appUser);

        HSETIRegulatorReviewReturnedForAmendsRequestActionPayload requestActionPayload = HSETI_MAPPER
                .toHSETIRegulatorReviewReturnedForAmendsRequestActionPayload(
                    reviewRequestTaskPayload,
                    RequestActionPayloadType.HSE_TI_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD
                );

        requestActionPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());

        requestService.addActionToRequest(request, requestActionPayload, RequestActionType.HSE_TI_REGULATOR_REVIEW_RETURNED_FOR_AMENDS,
                appUser.getUserId());
    }


    @Transactional
    public void updateRequestPayload(final RequestTask requestTask,
                                     final AppUser appUser) {
        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
            (HSETIApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        HSETIRequestPayload requestPayload = (HSETIRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setOverallDecision(reviewRequestTaskPayload.getOverallDecision());
        requestPayload.setRegulatorReviewGroupDecisions(reviewRequestTaskPayload.getRegulatorReviewGroupDecisions());
        requestPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());
        requestPayload.setRegulatorReviewSectionsCompleted(reviewRequestTaskPayload.getRegulatorReviewSectionsCompleted());

    }

    public void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser) {
        final HSETIRequestPayload requestPayload =
                (HSETIRequestPayload) requestTask.getRequest().getPayload();
        final HSETIApplicationRegulatorReviewSubmitRequestTaskPayload requestTaskPayload =
                (HSETIApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        requestPayload.setRegulatorReviewer(appUser.getUserId());

        final HSETI hseti = requestTaskPayload.getHseti();
        requestPayload.setHseti(hseti);
        requestPayload.setHsetiSectionsCompleted(requestTaskPayload.getHsetiSectionsCompleted());
        requestPayload.setHsetiAttachments(requestTaskPayload.getHsetiAttachments());
        requestPayload.setOverallDecision(requestTaskPayload.getOverallDecision());
        requestPayload.setRegulatorReviewGroupDecisions(requestTaskPayload.getRegulatorReviewGroupDecisions());
        requestPayload.setRegulatorReviewSectionsCompleted(requestTaskPayload.getRegulatorReviewSectionsCompleted());
        requestPayload.setRegulatorReviewAttachments(requestTaskPayload.getRegulatorReviewAttachments());
    }

}
