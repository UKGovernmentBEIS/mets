package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper.BDRMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation.BDRValidationService;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BDRSubmitService {

    private final BDRValidationService bdrValidationService;
    private final RequestService requestService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private static final BDRMapper BDR_MAPPER = Mappers.getMapper(BDRMapper.class);

    public void applySaveAction(RequestTask requestTask,
                                BDRApplicationSaveRequestTaskActionPayload taskActionPayload) {
        final BDRApplicationSubmitRequestTaskPayload taskPayload =
                (BDRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setBdrSectionsCompleted(
                taskActionPayload.getBdrSectionsCompleted());
        taskPayload.setBdr(taskActionPayload.getBdr());

        taskPayload.setVerificationPerformed(false);
    }

    public void submitToRegulator(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        BDRRequestPayload requestPayload = (BDRRequestPayload) request.getPayload();
        BDRRequestMetadata requestMetadata = (BDRRequestMetadata)  request.getMetadata();
        BDRApplicationSubmitRequestTaskPayload taskPayload = (BDRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        //Delete verificationReport if verificationPerformed = false as in this case verification report is obsolete.
        // If it is mandatory it is handled by VerificationPerformedRequestTaskActionValidator
        if (!taskPayload.isVerificationPerformed()) {
            requestPayload.setVerificationReport(null);
        }

        if (!ObjectUtils.isEmpty(requestPayload.getVerificationReport())) {
            bdrValidationService.validateVerificationReport(requestPayload.getVerificationReport());
        }
        bdrValidationService.validateBDR(taskPayload.getBdr());
        bdrValidationService.validateBDRFileName(taskPayload.getBdrAttachments().get(taskPayload.getBdr().getBdrFile()));

        RequestActionPayload actionPayload = createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, requestPayload, RequestActionPayloadType.BDR_APPLICATION_SUBMITTED_PAYLOAD);

        Optional.ofNullable(requestPayload.getVerificationReport()).ifPresent(report ->
             requestMetadata.setOverallAssessmentType(report.getVerificationData().getOverallAssessment().getType()));

        submitBDR(requestPayload, requestTask, appUser, RequestActionType.BDR_APPLICATION_SENT_TO_REGULATOR, actionPayload, taskPayload.getBdrSectionsCompleted());

    }

    public void submitToVerifier(BDRApplicationSubmitToVerifierRequestTaskActionPayload actionPayload,
                                 RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        BDRRequestPayload requestPayload = (BDRRequestPayload) request.getPayload();
        BDRApplicationSubmitRequestTaskPayload taskPayload = (BDRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        RequestActionPayload requestActionPayload = createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, requestPayload, RequestActionPayloadType.BDR_APPLICATION_SUBMITTED_PAYLOAD);

        bdrValidationService.validateBDR(taskPayload.getBdr());
        bdrValidationService.validateBDRFileName(taskPayload.getBdrAttachments().get(taskPayload.getBdr().getBdrFile()));

        requestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        submitBDR(requestPayload, requestTask, appUser, RequestActionType.BDR_APPLICATION_SENT_TO_VERIFIER, requestActionPayload, taskPayload.getBdrSectionsCompleted());
    }

    public BDRApplicationSubmittedRequestActionPayload createApplicationSubmittedRequestActionPayload(RequestTask requestTask,
                                                                                                        BDRApplicationSubmitRequestTaskPayload taskPayload,
                                                                                                        BDRRequestPayload requestPayload,
                                                                                                        RequestActionPayloadType payloadType) {

        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(requestTask.getRequest().getAccountId());

        BDRApplicationSubmittedRequestActionPayload actionPayload = BDR_MAPPER.toBDRApplicationSubmittedRequestActionPayload(taskPayload, payloadType);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        actionPayload.setBdrAttachments(taskPayload.getBdrAttachments());

        if (taskPayload.isVerificationPerformed()) {
            actionPayload.setVerificationReport(requestPayload.getVerificationReport());
            actionPayload.setVerificationAttachments(requestPayload.getVerificationAttachments());
        }

        return actionPayload;
    }

    public void submitBDR(BDRRequestPayload bdrRequestPayload,
                           RequestTask requestTask,
                           AppUser appUser,
                           RequestActionType requestActionType,
                           RequestActionPayload actionPayload,
                           Map<String, Boolean> bdrSectionsCompleted) {

        final BDRApplicationSubmitRequestTaskPayload taskPayload =
                (BDRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        bdrRequestPayload.setBdr(taskPayload.getBdr());
        bdrRequestPayload.setBdrAttachments(taskPayload.getBdrAttachments());
        bdrRequestPayload.setBdrSectionsCompleted(bdrSectionsCompleted);
        bdrRequestPayload.setVerificationPerformed(taskPayload.isVerificationPerformed());

        requestService.addActionToRequest(
                requestTask.getRequest(),
                actionPayload,
                requestActionType,
                appUser.getUserId());
    }

    public void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser) {
        final BDRRequestPayload requestPayload =
                (BDRRequestPayload) requestTask.getRequest().getPayload();
        final BDRApplicationRegulatorReviewSubmitRequestTaskPayload requestTaskPayload =
                (BDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        requestPayload.setRegulatorReviewer(appUser.getUserId());

        final BDR bdr = requestTaskPayload.getBdr();
        requestPayload.setBdr(bdr);
        requestPayload
                .setBdrSectionsCompleted(requestTaskPayload
                        .getBdrSectionsCompleted());
        requestPayload.setBdrAttachments(requestTaskPayload.getBdrAttachments());
        requestPayload.setRegulatorReviewAttachments(requestTaskPayload.getRegulatorReviewAttachments());
        requestPayload.setRegulatorReviewGroupDecisions(requestTaskPayload.getRegulatorReviewGroupDecisions());
        requestPayload.setRegulatorReviewSectionsCompleted(requestTaskPayload.getRegulatorReviewSectionsCompleted());
        requestPayload.setRegulatorReviewOutcome(requestTaskPayload.getRegulatorReviewOutcome());
        requestPayload.setVerificationAttachments(requestTaskPayload.getVerificationAttachments());
    }

}
