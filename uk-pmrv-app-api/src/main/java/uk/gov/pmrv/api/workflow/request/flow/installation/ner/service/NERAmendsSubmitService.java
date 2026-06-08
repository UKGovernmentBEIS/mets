package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NERValidationService;

@Service
@RequiredArgsConstructor
public class NERAmendsSubmitService {

    private final NerApplyService submitService;
    private final NERValidationService validationService;

    @Transactional
    public void saveAmends(NERApplicationAmendsSaveRequestTaskActionPayload taskActionPayload,
                           RequestTask requestTask) {
        NERApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
                (NERApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setNer(taskActionPayload.getNer());
        requestTaskPayload
                .setNerSectionsCompleted(taskActionPayload.getNerSectionsCompleted());
        requestTaskPayload
                .setRegulatorReviewSectionsCompleted(taskActionPayload.getRegulatorReviewSectionsCompleted());

        requestTaskPayload.setVerificationPerformed(false);
    }

    @Transactional
    public void submitToRegulator(NERApplicationAmendsSubmitRequestTaskActionPayload actionPayload,
                                  RequestTask requestTask, AppUser appUser) {
        NerRequestPayload requestPayload = (NerRequestPayload) requestTask.getRequest().getPayload();

        NERApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
                (NERApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setVerificationPerformed(requestPayload.isVerificationPerformed());
        validationService.validateAmendsVerification(requestPayload, requestTaskPayload);
        validationService.validateNer(requestTaskPayload.getNer());

        ((NERApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload())
                .setNerSectionsCompleted(actionPayload.getNerSectionsCompleted());

        ((NERApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload())
                .setRegulatorReviewSectionsCompleted(actionPayload.getRegulatorReviewSectionsCompleted());

        submitService.submitToRegulator(requestTask, appUser, RequestActionType.NER_APPLICATION_AMENDS_SUBMITTED);
    }

    public void sendAmendsToVerifier(NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload taskActionPayload,
                                     RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();
        NERApplicationAmendsSubmitRequestTaskPayload taskPayload = (NERApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        validationService.validateNer(taskPayload.getNer());

        requestPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());

        submitService.submitToVerifier(taskActionPayload, requestTask, appUser, RequestActionType.NER_APPLICATION_AMENDS_SENT_TO_VERIFIER);
    }
}
