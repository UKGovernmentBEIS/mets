package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRValidationService;


import java.util.Map;

@Service
@RequiredArgsConstructor
public class ALRRegulatorReviewSubmitService {

    private final ALRValidationService validationService;
    private final RequestService requestService;

    private static final ALRMapper ALR_MAPPER = Mappers.getMapper(ALRMapper.class);


    @Transactional
    public void saveReviewGroupDecision(final ALRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload payload,
                                        final RequestTask requestTask) {
        final ALRReviewGroup group = payload.getGroup();
        final ALRReviewDecision decision = payload.getDecision();

        final ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        final Map<ALRReviewGroup, ALRReviewDecision> reviewGroupDecisions = taskPayload.getRegulatorReviewGroupDecisions();

        reviewGroupDecisions.put(group, decision);

        final Map<String, Boolean> reviewSectionsCompleted = payload.getRegulatorReviewSectionsCompleted();
        taskPayload.setRegulatorReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void save(final ALRApplicationRegulatorReviewSaveTaskActionPayload payload,
                     final RequestTask requestTask) {

        final ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setRegulatorReviewOutcome(payload.getRegulatorReviewOutcome());

        final Map<String, Boolean> reviewSectionsCompleted = payload.getRegulatorReviewSectionsCompleted();
        taskPayload.setRegulatorReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void returnForAmends(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();

        ALRApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        validationService.validateReturnForAmends(reviewRequestTaskPayload);

        updateRequestPayload(requestTask, appUser);

        ALRRegulatorReviewReturnedForAmendsRequestActionPayload requestActionPayload = ALR_MAPPER
                .toALRRegulatorReviewReturnedForAmendsRequestActionPayload(
                        reviewRequestTaskPayload,
                        RequestActionPayloadType.ALR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD
                );

        requestActionPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());

        requestService.addActionToRequest(request, requestActionPayload, RequestActionType.ALR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS,
                appUser.getUserId());
    }

    @Transactional
    public void updateRequestPayload(final RequestTask requestTask,
                                     final AppUser appUser) {
        ALRApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setRegulatorReviewOutcome(reviewRequestTaskPayload.getRegulatorReviewOutcome());
        requestPayload.setRegulatorReviewGroupDecisions(reviewRequestTaskPayload.getRegulatorReviewGroupDecisions());
        requestPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());
        requestPayload.setRegulatorReviewSectionsCompleted(reviewRequestTaskPayload.getRegulatorReviewSectionsCompleted());
        requestPayload.setVerificationPerformed(!validationService.isVerificationRequiredFromReviewGroupDecisions(reviewRequestTaskPayload.getRegulatorReviewGroupDecisions()));

    }
}
