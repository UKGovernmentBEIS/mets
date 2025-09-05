package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRValidationService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ALRSubmitService {

    private final ALRValidationService alrValidationService;
    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private static final ALRMapper ALR_MAPPER = Mappers.getMapper(ALRMapper.class);



    public void applySaveAction(RequestTask requestTask,
                                ALRApplicationSaveRequestTaskActionPayload taskActionPayload) {
        final ALRApplicationSubmitRequestTaskPayload taskPayload =
                (ALRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setAlrSectionsCompleted(
                taskActionPayload.getAlrSectionsCompleted());
        taskPayload.setAlr(taskActionPayload.getAlr());

        taskPayload.setVerificationPerformed(false);
    }

    public void submitToVerifier(ALRApplicationSubmitToVerifierRequestTaskActionPayload actionPayload,
                                 RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();
        ALRApplicationSubmitRequestTaskPayload taskPayload = (ALRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        RequestActionPayload requestActionPayload = createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, requestPayload, RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD);

        alrValidationService.validateALR(taskPayload.getAlr());
        alrValidationService.validateALRFileName(taskPayload.getAlrAttachments().get(taskPayload.getAlr().getAlrFile()));

        requestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        submitALR(requestPayload, requestTask, appUser, RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER, requestActionPayload, taskPayload.getAlrSectionsCompleted());
    }

    public void submitToRegulator(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();
        ALRApplicationSubmitRequestTaskPayload taskPayload = (ALRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        alrValidationService.validateALR(taskPayload.getAlr());

        RequestActionPayload actionPayload = createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, requestPayload, RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD);

        submitALR(requestPayload, requestTask, appUser, RequestActionType.ALR_APPLICATION_SENT_TO_REGULATOR, actionPayload, taskPayload.getAlrSectionsCompleted());

    }
    public void notifyOperator(RequestTask requestTask,
                               final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {
        Request request = requestTask.getRequest();
        ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();
        final ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        // Update request
        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);
        requestPayload.setDecisionNotification(taskActionPayload.getDecisionNotification());
        updateRequestPayload(requestPayload, taskPayload);
    }

    public void complete(RequestTask requestTask) {
        Request request = requestTask.getRequest();
        ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();
        final ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        // Update request
        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);
        updateRequestPayload(requestPayload, taskPayload);
    }

    public void addProceededToAuthorityRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();

        ALRApplicationProceededToAuthorityRequestActionPayload actionPayload = ALR_MAPPER
                .toALRApplicationProceededToAuthorityRequestActionPayload(requestPayload);

        // Add users info if decision notification exists
        final DecisionNotification notification = requestPayload.getDecisionNotification();
        if(notification != null) {
            final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
                    .getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
            actionPayload.setUsersInfo(usersInfo);
        }

        // Add to request
        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.ALR_APPLICATION_PROCEEDED_TO_AUTHORITY,
                requestPayload.getRegulatorAssignee());
    }

    public ALRApplicationSubmittedRequestActionPayload createApplicationSubmittedRequestActionPayload(RequestTask requestTask,
                                                                                                      ALRApplicationSubmitRequestTaskPayload taskPayload,
                                                                                                      ALRRequestPayload requestPayload,
                                                                                                      RequestActionPayloadType payloadType) {

        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(requestTask.getRequest().getAccountId());

        ALRApplicationSubmittedRequestActionPayload actionPayload = ALR_MAPPER.toALRApplicationSubmittedRequestActionPayload(taskPayload, payloadType);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        actionPayload.setAlrAttachments(taskPayload.getAlrAttachments());

        if (taskPayload.isVerificationPerformed()) {
            actionPayload.setVerificationReport(requestPayload.getVerificationReport());
            actionPayload.setVerificationAttachments(requestPayload.getVerificationAttachments());
        }

        return actionPayload;
    }

    public void submitALR(ALRRequestPayload alrRequestPayload,
                          RequestTask requestTask,
                          AppUser appUser,
                          RequestActionType requestActionType,
                          RequestActionPayload actionPayload,
                          Map<String, Boolean> alrSectionsCompleted) {

        final ALRApplicationSubmitRequestTaskPayload taskPayload =
                (ALRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        if (!ObjectUtils.isEmpty(taskPayload.getAlr())) {
            ALR alrRequest = alrRequestPayload.getAlr();
            ALR taskAlr = taskPayload.getAlr();

            boolean shouldIncrement = alrRequest == null ||
                    alrRequest.getAlrFile() == null ||
                    !Objects.equals(alrRequest.getAlrFile(), taskAlr.getAlrFile());

            if (shouldIncrement) {
                alrRequestPayload.incrementAlrFileVersion();
            }
        }

        alrRequestPayload.setAlr(taskPayload.getAlr());
        alrRequestPayload.setAlrAttachments(taskPayload.getAlrAttachments());
        alrRequestPayload.setAlrSectionsCompleted(alrSectionsCompleted);
        alrRequestPayload.setVerificationPerformed(taskPayload.isVerificationPerformed());

        requestService.addActionToRequest(
                requestTask.getRequest(),
                actionPayload,
                requestActionType,
                appUser.getUserId());
    }

    public void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser) {
        final ALRRequestPayload requestPayload =
                (ALRRequestPayload) requestTask.getRequest().getPayload();
        final ALRApplicationRegulatorReviewSubmitRequestTaskPayload requestTaskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        requestPayload.setRegulatorReviewer(appUser.getUserId());

        final ALR alr = requestTaskPayload.getAlr();
        requestPayload.setAlr(alr);
        requestPayload.setAlrSectionsCompleted(requestTaskPayload.getAlrSectionsCompleted());
        requestPayload.setAlrAttachments(requestTaskPayload.getAlrAttachments());
        requestPayload.setRegulatorReviewOutcome(requestTaskPayload.getRegulatorReviewOutcome());
        requestPayload.setRegulatorReviewAttachments(requestTaskPayload.getRegulatorReviewAttachments());
        requestPayload.setRegulatorReviewGroupDecisions(requestTaskPayload.getRegulatorReviewGroupDecisions());
        requestPayload.setRegulatorReviewSectionsCompleted(requestTaskPayload.getRegulatorReviewSectionsCompleted());
    }

    private void updateRequestPayload(ALRRequestPayload requestPayload, ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {
        requestPayload.setAlr(taskPayload.getAlr());
        requestPayload.setAlrSectionsCompleted(taskPayload.getAlrSectionsCompleted());
        requestPayload.setAlrAttachments(taskPayload.getAlrAttachments());
        requestPayload.setRegulatorReviewOutcome(taskPayload.getRegulatorReviewOutcome());
        requestPayload.setRegulatorReviewAttachments(taskPayload.getRegulatorReviewAttachments());
    }
}
