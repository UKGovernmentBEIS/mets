package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation.BDRValidationService;

@Service
@RequiredArgsConstructor
public class BDRAmendsSubmitService {

    private final BDRSubmitService submitService;
    private final BDRValidationService validationService;

    @Transactional
    public void saveAmends(BDRApplicationAmendsSaveRequestTaskActionPayload taskActionPayload,
                           RequestTask requestTask) {
        BDRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
            (BDRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setBdr(taskActionPayload.getBdr());
        requestTaskPayload
            .setBdrSectionsCompleted(taskActionPayload.getBdrSectionsCompleted());
        requestTaskPayload
            .setRegulatorReviewSectionsCompleted(taskActionPayload.getRegulatorReviewSectionsCompleted());

        requestTaskPayload.setVerificationPerformed(false);
    }

    @Transactional
    public void submitToRegulator(BDRApplicationAmendsSubmitRequestTaskActionPayload actionPayload,
                                  RequestTask requestTask, AppUser appUser) {
        BDRRequestPayload requestPayload = (BDRRequestPayload) requestTask.getRequest().getPayload();

        BDRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
                (BDRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setVerificationPerformed(requestPayload.isVerificationPerformed());
        validationService.validateAmendsVerification(requestPayload,requestTaskPayload);

        ((BDRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload())
                .setBdrSectionsCompleted(actionPayload.getBdrSectionsCompleted());

        submitService.submitToRegulator(requestTask, appUser);
    }

     public void sendAmendsToVerifier(BDRApplicationAmendsSubmitToVerifierRequestTaskActionPayload actionPayload,
                                      RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        BDRRequestPayload requestPayload = (BDRRequestPayload) request.getPayload();
        BDRApplicationAmendsSubmitRequestTaskPayload taskPayload = (BDRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorReviewSectionsCompleted(taskPayload.getRegulatorReviewSectionsCompleted());

        RequestActionPayload requestActionPayload = submitService.createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, requestPayload, RequestActionPayloadType.BDR_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);

        validationService.validateBDR(taskPayload.getBdr());

        requestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        submitService.submitBDR(requestPayload, requestTask, appUser, RequestActionType.BDR_APPLICATION_AMENDS_SENT_TO_VERIFIER, requestActionPayload, taskPayload.getBdrSectionsCompleted());
    }

}
