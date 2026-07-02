package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERSaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERFiles;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewOpinion;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NERMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NERValidationService;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NERRegulatorReviewSubmitService {

    private final NERValidationService validationService;
    private final RequestService requestService;
    private static final NERMapper NER_MAPPER = Mappers.getMapper(NERMapper.class);
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestVerificationService requestVerificationService;

    @Transactional
    public void saveReviewGroupDecision(final NERSaveRegulatorReviewGroupDecisionRequestTaskActionPayload payload,
                                        final RequestTask requestTask) {
        final NERReviewGroup group = payload.getGroup();
        final NERReviewDecision decision = payload.getDecision();

        final NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (NERApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        final Map<NERReviewGroup, NERReviewDecision> reviewGroupDecisions = taskPayload.getRegulatorReviewGroupDecisions();

        reviewGroupDecisions.put(group, decision);

        final Map<String, Boolean> reviewSectionsCompleted = payload.getRegulatorReviewSectionsCompleted();
        taskPayload.setRegulatorReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void returnForAmends(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
                (NERApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        validationService.validateReturnForAmends(reviewRequestTaskPayload);

        updateRequestPayload(requestTask, appUser);

        NERRegulatorReviewReturnedForAmendsRequestActionPayload requestActionPayload = NER_MAPPER
                .toNERRegulatorReviewReturnedForAmendsRequestActionPayload(
                        reviewRequestTaskPayload,
                        RequestActionPayloadType.NER_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
                );

        requestActionPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());

        requestService.addActionToRequest(request, requestActionPayload, RequestActionType.NER_APPLICATION_RETURNED_FOR_AMENDS,
                appUser.getUserId());
    }

    @Transactional
    public void completeApplication(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        NERApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
                (NERApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        validationService.validateRegulatorReviewOutcome(reviewRequestTaskPayload, NERReviewOpinion.PROCEED_TO_AUTHORITY);

        updateRequestPayload(requestTask, appUser);
        addRequestAction(request.getId(), RequestActionPayloadType.NER_APPLICATION_COMPLETED_PAYLOAD, RequestActionType.NER_APPLICATION_COMPLETED);
    }


    @Transactional
    public void prepareRequestPayloadForReopening(NerRequestPayload requestPayload){
        UUID regulatorNerFile = requestPayload.getRegulatorReviewOutcome().getNerFile();
        if (regulatorNerFile != null) {
            incrementNERFileVersionAndUpdateFile(regulatorNerFile, requestPayload);
        }
    }

    @Transactional
    public void deemWithdrawn(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
                (NERApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        validationService.validateRegulatorReviewOutcome(reviewRequestTaskPayload, NERReviewOpinion.WITHDRAW);

        updateRequestPayload(requestTask, appUser);

        addRequestAction(request.getId(), RequestActionPayloadType.NER_APPLICATION_DEEM_WITHDRAWN_PAYLOAD, RequestActionType.NER_APPLICATION_DEEMED_WITHDRAWN);

    }

    @Transactional
    public void save(final NERApplicationRegulatorReviewSaveTaskActionPayload payload,
                     final RequestTask requestTask) {

        final NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (NERApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setRegulatorReviewOutcome(payload.getRegulatorReviewOutcome());

        final Map<String, Boolean> reviewSectionsCompleted = payload.getRegulatorReviewSectionsCompleted();
        taskPayload.setRegulatorReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void updateRequestPayload(final RequestTask requestTask,
                                     final AppUser appUser) {
        NERApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
                (NERApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setRegulatorReviewOutcome(reviewRequestTaskPayload.getRegulatorReviewOutcome());
        requestPayload.setRegulatorReviewGroupDecisions(reviewRequestTaskPayload.getRegulatorReviewGroupDecisions());
        requestPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());
        requestPayload.setRegulatorReviewSectionsCompleted(reviewRequestTaskPayload.getRegulatorReviewSectionsCompleted());
        requestPayload.setNerSectionsCompleted(reviewRequestTaskPayload.getNerSectionsCompleted());
    }

    public void addRequestAction(final String requestId, RequestActionPayloadType requestActionPayloadType, RequestActionType requestActionType) {
        final Request request = requestService.findRequestById(requestId);
        final NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();


        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());

        requestVerificationService.refreshVerificationReportVBDetails(requestPayload.getVerificationReport(),
                request.getVerificationBodyId());

        final NERApplicationCompletedRequestActionPayload actionPayload =
                NER_MAPPER.toNERApplicationCompletedRequestActionPayload(requestPayload,
                        installationOperatorDetails,
                        requestPayload.getVerificationReport(),
                        requestActionPayloadType);

        requestService.addActionToRequest(request,
                actionPayload,
                requestActionType,
                requestPayload.getRegulatorReviewer());
    }

    private void incrementNERFileVersionAndUpdateFile(UUID regulatorNerFile, NerRequestPayload requestPayload) {
        UUID requestFile = Optional.ofNullable(requestPayload.getNer())
                .map(NER::getNerFiles)
                .map(NERFiles::getFile)
                .orElse(null);

        if (!Objects.equals(requestFile, regulatorNerFile)) {
            requestPayload.incrementNerFileVersion();

            //regulator ner file is transferred to operator side
            requestPayload.getNer().getNerFiles().setFile(regulatorNerFile);
            String fileName = requestPayload.getRegulatorReviewAttachments().get(regulatorNerFile);
            requestPayload.getNerAttachments().put(regulatorNerFile, fileName);

            //and removed from regulator side
            requestPayload.getRegulatorReviewAttachments().remove(regulatorNerFile);
            requestPayload.getRegulatorReviewOutcome().setNerFile(null);
        }
    }

}

