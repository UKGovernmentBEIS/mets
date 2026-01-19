package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRValidationService;

@Service
@RequiredArgsConstructor
public class ALRAmendsSubmitService {

    private final ALRSubmitService submitService;
    private final ALRValidationService validationService;

    @Transactional
    public void saveAmends(ALRApplicationAmendsSaveRequestTaskActionPayload taskActionPayload,
                           RequestTask requestTask) {
        ALRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
                (ALRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setAlr(taskActionPayload.getAlr());
        requestTaskPayload
                .setAlrSectionsCompleted(taskActionPayload.getAlrSectionsCompleted());
        requestTaskPayload
                .setRegulatorReviewSectionsCompleted(taskActionPayload.getRegulatorReviewSectionsCompleted());

        requestTaskPayload.setVerificationPerformed(false);
    }

    @Transactional
    public void submitToRegulator(ALRApplicationAmendsSubmitRequestTaskActionPayload actionPayload,
                                  RequestTask requestTask, AppUser appUser) {
        ALRRequestPayload requestPayload = (ALRRequestPayload) requestTask.getRequest().getPayload();

        ALRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
                (ALRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setVerificationPerformed(requestPayload.isVerificationPerformed());
        validationService.validateAmendsVerification(requestPayload, requestTaskPayload);
        validationService.validateALR(requestTaskPayload.getAlr());

        ((ALRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload())
                .setAlrSectionsCompleted(actionPayload.getAlrSectionsCompleted());

        RequestActionPayload requestActionPayload = submitService.createApplicationSubmittedRequestActionPayload(
                requestTask, requestTaskPayload, requestPayload, RequestActionPayloadType.ALR_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);

        submitService.submitALR(requestPayload, requestTask, appUser,
                RequestActionType.ALR_APPLICATION_AMENDS_SUBMITTED,
                requestActionPayload, requestTaskPayload.getAlrSectionsCompleted(), true);
    }

    public void sendAmendsToVerifier(ALRApplicationAmendsSubmitToVerifierRequestTaskActionPayload actionPayload,
                                     RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();
        ALRApplicationAmendsSubmitRequestTaskPayload taskPayload = (ALRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorReviewSectionsCompleted(taskPayload.getRegulatorReviewSectionsCompleted());

        RequestActionPayload requestActionPayload = submitService.createApplicationSubmittedRequestActionPayload(
                requestTask, taskPayload, requestPayload, RequestActionPayloadType.ALR_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);

        validationService.validateALR(taskPayload.getAlr());

        requestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        submitService.submitALR(requestPayload, requestTask, appUser,
                RequestActionType.ALR_APPLICATION_AMENDS_SENT_TO_VERIFIER,
                requestActionPayload, taskPayload.getAlrSectionsCompleted(), false);
    }
}
