package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;

import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper.BDRMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation.BDRValidationService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BDRRegulatorReviewSubmitService {

    private final BDRValidationService validationService;
    private final RequestService requestService;

    private static final BDRMapper BDR_MAPPER = Mappers.getMapper(BDRMapper.class);

    @Transactional
    public void submit( final RequestTask requestTask, final AppUser appUser) {
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
            (BDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        BDRRequestPayload requestPayload =
                (BDRRequestPayload) requestTask.getRequest().getPayload();


        validationService.validateRegulatorReviewGroupDecisions(taskPayload.getRegulatorReviewGroupDecisions(),requestPayload.isVerificationPerformed());
        validationService.validateRegulatorReviewOutcome(taskPayload.getRegulatorReviewOutcome());

        updateRequestPayload(requestTask, appUser);
    }

    @Transactional
    public void save(final BDRApplicationRegulatorReviewSaveTaskActionPayload payload,
                                        final RequestTask requestTask) {

        final BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
            (BDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setRegulatorReviewOutcome(payload.getRegulatorReviewOutcome());

        final Map<String, Boolean> reviewSectionsCompleted = payload.getRegulatorReviewSectionsCompleted();
        taskPayload.setRegulatorReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void saveReviewGroupDecision(final BDRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload payload,
                                        final RequestTask requestTask) {
        final BDRReviewGroup group = payload.getGroup();
        final BDRReviewDecision decision = payload.getDecision();

        final BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
            (BDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        final Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = taskPayload.getRegulatorReviewGroupDecisions();

        reviewGroupDecisions.put(group, decision);

        final Map<String, Boolean> reviewSectionsCompleted = payload.getRegulatorReviewSectionsCompleted();
        taskPayload.setRegulatorReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void returnForAmends(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();

        BDRApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
            (BDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        validationService.validateReturnForAmends(reviewRequestTaskPayload);

        updateRequestPayload(requestTask, appUser);

        BDRRegulatorReviewReturnedForAmendsRequestActionPayload requestActionPayload = BDR_MAPPER
                .toBDRRegulatorReviewReturnedForAmendsRequestActionPayload(
                    reviewRequestTaskPayload,
                    RequestActionPayloadType.BDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD
                );

        requestActionPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());

        requestService.addActionToRequest(request, requestActionPayload, RequestActionType.BDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS,
                appUser.getUserId());
    }

    @Transactional
    public void updateRequestPayload(final RequestTask requestTask,
                                     final AppUser appUser) {
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
            (BDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        BDRRequestPayload requestPayload = (BDRRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setRegulatorReviewOutcome(reviewRequestTaskPayload.getRegulatorReviewOutcome());
        requestPayload.setRegulatorReviewGroupDecisions(reviewRequestTaskPayload.getRegulatorReviewGroupDecisions());
        requestPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());
        requestPayload.setRegulatorReviewSectionsCompleted(reviewRequestTaskPayload.getRegulatorReviewSectionsCompleted());

    }

}
