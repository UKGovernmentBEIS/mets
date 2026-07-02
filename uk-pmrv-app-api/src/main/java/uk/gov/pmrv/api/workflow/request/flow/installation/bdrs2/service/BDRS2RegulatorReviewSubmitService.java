package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2SaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Files;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper.BDRS2Mapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation.BDRS2ValidationService;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BDRS2RegulatorReviewSubmitService {

    private final BDRS2ValidationService validationService;
    private final RequestService requestService;

    private static final BDRS2Mapper BDRS2_MAPPER = Mappers.getMapper(BDRS2Mapper.class);

    @Transactional
    public void submit(final RequestTask requestTask, final AppUser appUser) {
        BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
            (BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        BDRS2RequestPayload requestPayload =
                (BDRS2RequestPayload) requestTask.getRequest().getPayload();

        validationService.validateRegulatorReviewGroupDecisions(taskPayload.getRegulatorReviewGroupDecisions(), requestPayload.isVerificationPerformed());
        validationService.validateRegulatorReviewOutcome(taskPayload);
        updateRequestPayload(requestTask, appUser);

    }

    @Transactional
    public void prepareRequestPayloadForReopening(BDRS2RequestPayload requestPayload){
        UUID regulatorBdrs2File = requestPayload.getRegulatorReviewOutcome().getFile();
        if (regulatorBdrs2File != null) {
            incrementBdrs2FileVersionAndUpdateFile(regulatorBdrs2File, requestPayload);
        }
    }

    @Transactional
    public void save(final BDRS2ApplicationRegulatorReviewSaveTaskActionPayload payload,
                     final RequestTask requestTask) {

        final BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
            (BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setRegulatorReviewOutcome(payload.getRegulatorReviewOutcome());

        final Map<String, Boolean> reviewSectionsCompleted = payload.getRegulatorReviewSectionsCompleted();
        taskPayload.setRegulatorReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void saveReviewGroupDecision(final BDRS2SaveRegulatorReviewGroupDecisionRequestTaskActionPayload payload,
                                        final RequestTask requestTask) {
        final BDRS2ReviewGroup group = payload.getGroup();
        final BDRS2ReviewDecision decision = payload.getDecision();

        final BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
            (BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        final Map<BDRS2ReviewGroup, BDRS2ReviewDecision> reviewGroupDecisions = taskPayload.getRegulatorReviewGroupDecisions();

        reviewGroupDecisions.put(group, decision);

        final Map<String, Boolean> reviewSectionsCompleted = payload.getRegulatorReviewSectionsCompleted();
        taskPayload.setRegulatorReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void returnForAmends(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();

        BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
            (BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        validationService.validateReturnForAmends(reviewRequestTaskPayload);

        updateRequestPayload(requestTask, appUser);

        BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload requestActionPayload = BDRS2_MAPPER
                .toBDRS2RegulatorReviewReturnedForAmendsRequestActionPayload(
                    reviewRequestTaskPayload,
                    RequestActionPayloadType.BDRS2_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD
                );

        requestActionPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());

        requestService.addActionToRequest(request, requestActionPayload, RequestActionType.BDRS2_REGULATOR_REVIEW_RETURNED_FOR_AMENDS,
                appUser.getUserId());
    }

    @Transactional
    public void updateRequestPayload(final RequestTask requestTask,
                                     final AppUser appUser) {
        BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
            (BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        BDRS2RequestPayload requestPayload = (BDRS2RequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setRegulatorReviewOutcome(reviewRequestTaskPayload.getRegulatorReviewOutcome());
        requestPayload.setRegulatorReviewGroupDecisions(reviewRequestTaskPayload.getRegulatorReviewGroupDecisions());
        requestPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());
        requestPayload.setRegulatorReviewSectionsCompleted(reviewRequestTaskPayload.getRegulatorReviewSectionsCompleted());
        requestPayload.setBdrs2SectionsCompleted(reviewRequestTaskPayload.getBdrs2SectionsCompleted());
    }

    private void incrementBdrs2FileVersionAndUpdateFile(UUID regulatorBdrs2File, BDRS2RequestPayload requestPayload) {
        UUID requestFile = Optional.ofNullable(requestPayload.getBdrs2())
                .map(BDRS2::getBdrs2Files)
                .map(BDRS2Files::getFile)
                .orElse(null);

        if (!Objects.equals(requestFile, regulatorBdrs2File)) {
            requestPayload.incrementBdrs2FileVersion();

            //regulator bdrs2 file is transferred to operator side
            requestPayload.getBdrs2().getBdrs2Files().setFile(regulatorBdrs2File);
            String fileName = requestPayload.getRegulatorReviewAttachments().get(regulatorBdrs2File);
            requestPayload.getBdrs2Attachments().put(regulatorBdrs2File, fileName);

            //and removed from regulator side
            requestPayload.getRegulatorReviewAttachments().remove(regulatorBdrs2File);
            requestPayload.getRegulatorReviewOutcome().setFile(null);
        }
    }
}
