package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation.BDRS2ValidationService;

@Service
@RequiredArgsConstructor
public class BDRS2AmendsSubmitService {

    private final BDRS2SubmitService submitService;
    private final BDRS2ValidationService validationService;

    @Transactional
    public void saveAmends(BDRS2ApplicationAmendsSaveRequestTaskActionPayload taskActionPayload,
                           RequestTask requestTask) {
        BDRS2ApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
            (BDRS2ApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setBdrs2(taskActionPayload.getBdrs2());
        requestTaskPayload
            .setBdrs2SectionsCompleted(taskActionPayload.getBdrs2SectionsCompleted());
        requestTaskPayload
            .setRegulatorReviewSectionsCompleted(taskActionPayload.getRegulatorReviewSectionsCompleted());

        requestTaskPayload.setVerificationPerformed(false);
    }

    @Transactional
    public void submitToRegulator(BDRS2ApplicationAmendsSubmitRequestTaskActionPayload actionPayload,
                                  RequestTask requestTask, AppUser appUser) {
        BDRS2RequestPayload requestPayload = (BDRS2RequestPayload) requestTask.getRequest().getPayload();

        BDRS2ApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
                (BDRS2ApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setVerificationPerformed(requestPayload.isVerificationPerformed());
        validationService.validateAmendsVerification(requestPayload, requestTaskPayload);

        ((BDRS2ApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload())
                .setBdrs2SectionsCompleted(actionPayload.getBdrs2SectionsCompleted());

        ((BDRS2ApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload())
                .setRegulatorReviewSectionsCompleted(actionPayload.getRegulatorReviewSectionsCompleted());

        submitService.submitToRegulator(requestTask, appUser);
    }

     public void sendAmendsToVerifier(BDRS2ApplicationAmendsSubmitToVerifierRequestTaskActionPayload actionPayload,
                                      RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        BDRS2RequestPayload requestPayload = (BDRS2RequestPayload) request.getPayload();
        BDRS2ApplicationAmendsSubmitRequestTaskPayload taskPayload = (BDRS2ApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorReviewSectionsCompleted(taskPayload.getRegulatorReviewSectionsCompleted());

        RequestActionPayload requestActionPayload = submitService.createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, requestPayload, RequestActionPayloadType.BDRS2_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);

        validationService.validateBDRS2(taskPayload.getBdrs2());

        requestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        submitService.submitBDRS2(requestPayload, requestTask, appUser, RequestActionType.BDRS2_APPLICATION_AMENDS_SENT_TO_VERIFIER, requestActionPayload, taskPayload.getBdrs2SectionsCompleted());
    }

}
