package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERFiles;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NERMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NERValidationService;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NerApplyService {

    private final RequestService requestService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final NERValidationService nerValidationService;
    private static final NERMapper NER_MAPPER = Mappers.getMapper(NERMapper.class);

    @Transactional
    public void applySaveAction(final RequestTask requestTask,
                                final NerSaveApplicationRequestTaskActionPayload taskActionPayload) {

        final NerApplicationSubmitRequestTaskPayload
            requestTaskPayload = (NerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setNerSectionsCompleted(taskActionPayload.getNerSectionsCompleted());
        requestTaskPayload.setNer(taskActionPayload.getNer());
        requestTaskPayload.setNerFileVersion(taskActionPayload.getNerFileVersion());


        //on any change, ner should be verified again
        requestTaskPayload.setVerificationPerformed(false);
    }

    public void submitToVerifier(NERApplicationSubmitToVerifierRequestTaskActionPayload actionPayload,
                                 RequestTask requestTask, AppUser appUser, RequestActionType requestActionType) {
        Request request = requestTask.getRequest();
        NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();
        NerApplicationSubmitRequestTaskPayload taskPayload = (NerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate NER data
        nerValidationService.validateNer(taskPayload.getNer());

        // Validate NER file name
        if (taskPayload.getNer().getNerFiles() != null) {
            String fileName = taskPayload.getNerAttachments().get(taskPayload.getNer().getNerFiles().getFile());
            nerValidationService.validateNerFileName(fileName);
        }

        requestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        incrementNerFileVersion(taskPayload, requestPayload);

        // Create request action payload
        NERApplicationSubmittedRequestActionPayload requestActionPayload =
                createApplicationSubmittedRequestActionPayload(
                        requestTask,
                        taskPayload,
                        requestPayload,
                        RequestActionPayloadType.NER_APPLICATION_SUBMITTED_PAYLOAD);

        // Save NER data to request payload
        requestPayload.setNer(taskPayload.getNer());
        requestPayload.setNerAttachments(taskPayload.getNerAttachments());
        requestPayload.setNerSectionsCompleted(taskPayload.getNerSectionsCompleted());

        // Add request action
        requestService.addActionToRequest(
                request,
                requestActionPayload,
                requestActionType,
                appUser.getUserId());
    }

    public void submitToRegulator(RequestTask requestTask, AppUser appUser, RequestActionType requestActionType) {
        Request request = requestTask.getRequest();
        NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();
        NERRequestMetadata requestMetadata = (NERRequestMetadata)  request.getMetadata();
        NerApplicationSubmitRequestTaskPayload taskPayload = (NerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Delete verificationReport if verificationPerformed = false
        if (!taskPayload.isVerificationPerformed()) {
            requestPayload.setVerificationReport(null);
        }

        // Validate verification report if verification was performed
        if (!ObjectUtils.isEmpty(requestPayload.getVerificationReport())) {
            nerValidationService.validateVerificationReport(requestPayload.getVerificationReport());
        }

        // Validate BDRS2 data
        nerValidationService.validateNer(taskPayload.getNer());

        // Validate BDRS2 file name
        if (taskPayload.getNer().getNerFiles() != null) {
            String fileName = taskPayload.getNerAttachments().get(taskPayload.getNer().getNerFiles().getFile());
            nerValidationService.validateNerFileName(fileName);
        }

        incrementNerFileVersion(taskPayload, requestPayload);

        Optional.ofNullable(requestPayload.getVerificationReport()).ifPresent(report ->
                requestMetadata.setOverallAssessmentType(report.getVerificationData().getOverallAssessment().getType()));

        // Create request action payload
        NERApplicationSubmittedRequestActionPayload actionPayload =
                createApplicationSubmittedRequestActionPayload(
                        requestTask,
                        taskPayload,
                        requestPayload,
                        RequestActionPayloadType.NER_APPLICATION_SUBMITTED_PAYLOAD);

        // Save BDRS2 data to request payload
        requestPayload.setNer(taskPayload.getNer());
        requestPayload.setNerAttachments(taskPayload.getNerAttachments());
        requestPayload.setNerSectionsCompleted(taskPayload.getNerSectionsCompleted());
        requestPayload.setVerificationPerformed(taskPayload.isVerificationPerformed());
        requestPayload.setRegulatorReviewSectionsCompleted(taskPayload.getRegulatorReviewSectionsCompleted());

        // Add request action
        requestService.addActionToRequest(
                request,
                actionPayload,
                requestActionType,
                appUser.getUserId());
    }

    public void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser) {
        final NerRequestPayload requestPayload =
                (NerRequestPayload) requestTask.getRequest().getPayload();
        final NERApplicationRegulatorReviewSubmitRequestTaskPayload requestTaskPayload =
                (NERApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        requestPayload.setRegulatorReviewer(appUser.getUserId());

        final NER ner = requestTaskPayload.getNer();
        requestPayload.setNer(ner);
        requestPayload.setNerSectionsCompleted(requestTaskPayload.getNerSectionsCompleted());
        requestPayload.setNerAttachments(requestTaskPayload.getNerAttachments());
        requestPayload.setRegulatorReviewOutcome(requestTaskPayload.getRegulatorReviewOutcome());
        requestPayload.setRegulatorReviewAttachments(requestTaskPayload.getRegulatorReviewAttachments());
        requestPayload.setRegulatorReviewGroupDecisions(requestTaskPayload.getRegulatorReviewGroupDecisions());
        requestPayload.setRegulatorReviewSectionsCompleted(requestTaskPayload.getRegulatorReviewSectionsCompleted());
    }

    public NERApplicationSubmittedRequestActionPayload createApplicationSubmittedRequestActionPayload(
            RequestTask requestTask,
            NerApplicationSubmitRequestTaskPayload taskPayload,
            NerRequestPayload requestPayload,
            RequestActionPayloadType payloadType) {

        InstallationOperatorDetails installationOperatorDetails =
                installationOperatorDetailsQueryService.getInstallationOperatorDetails(requestTask.getRequest().getAccountId());

        NERApplicationSubmittedRequestActionPayload actionPayload =
                NER_MAPPER.toNERApplicationSubmittedRequestActionPayload(taskPayload, payloadType);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        actionPayload.setNerAttachments(taskPayload.getNerAttachments());

        if (taskPayload.isVerificationPerformed()) {
            actionPayload.setVerificationReport(requestPayload.getVerificationReport());
            actionPayload.setVerificationAttachments(requestPayload.getVerificationAttachments());
        }

        return actionPayload;
    }

    private void incrementNerFileVersion(NerApplicationSubmitRequestTaskPayload taskPayload, NerRequestPayload requestPayload) {
        UUID requestFile = Optional.ofNullable(requestPayload.getNer().getNerFiles())
                .map(NERFiles::getFile)
                .orElse(null);

        UUID taskFile = Optional.ofNullable(taskPayload.getNer().getNerFiles())
                .map(NERFiles::getFile)
                .orElse(null);

        if (!Objects.equals(requestFile, taskFile)) {
            requestPayload.incrementNerFileVersion();
        }
    }
}
