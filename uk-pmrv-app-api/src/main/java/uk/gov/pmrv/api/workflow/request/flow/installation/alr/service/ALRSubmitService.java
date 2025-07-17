package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRValidationService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ALRSubmitService {

    private final ALRValidationService alrValidationService;
    private final RequestService requestService;
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
}
