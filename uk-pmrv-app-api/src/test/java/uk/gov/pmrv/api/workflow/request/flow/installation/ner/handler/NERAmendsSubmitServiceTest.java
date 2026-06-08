package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERAmendsSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NERValidationService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NERAmendsSubmitServiceTest {

    @Mock
    private NerApplyService submitService;

    @Mock
    private NERValidationService validationService;

    @InjectMocks
    private NERAmendsSubmitService service;

    @Test
    void saveAmends() {
        // given
        NERApplicationAmendsSaveRequestTaskActionPayload actionPayload =
                NERApplicationAmendsSaveRequestTaskActionPayload.builder()
                        .ner(new NER())
                        .nerSectionsCompleted(Map.of("section", true))
                        .regulatorReviewSectionsCompleted(Map.of("review", true))
                        .build();

        NERApplicationAmendsSubmitRequestTaskPayload taskPayload =
                NERApplicationAmendsSubmitRequestTaskPayload.builder()
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        // when
        service.saveAmends(actionPayload, requestTask);

        // then
        assertEquals(actionPayload.getNer(), taskPayload.getNer());
        assertEquals(actionPayload.getNerSectionsCompleted(),
                taskPayload.getNerSectionsCompleted());
        assertEquals(actionPayload.getRegulatorReviewSectionsCompleted(),
                taskPayload.getRegulatorReviewSectionsCompleted());
        assertFalse(taskPayload.isVerificationPerformed());
    }

    @Test
    void submitToRegulator() {
        // given
        NERApplicationAmendsSubmitRequestTaskActionPayload actionPayload =
                NERApplicationAmendsSubmitRequestTaskActionPayload.builder()
                        .nerSectionsCompleted(Map.of("section", true))
                        .build();

        NerRequestPayload requestPayload = NerRequestPayload.builder()
                .verificationPerformed(true)
                .build();

        NERApplicationAmendsSubmitRequestTaskPayload taskPayload =
                NERApplicationAmendsSubmitRequestTaskPayload.builder()
                        .ner(new NER())
                        .build();

        Request request = Request.builder()
                .payload(requestPayload)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        AppUser appUser = AppUser.builder().build();

        // when
        service.submitToRegulator(actionPayload, requestTask, appUser);

        // then
        assertTrue(taskPayload.isVerificationPerformed());

        verify(validationService)
                .validateAmendsVerification(requestPayload, taskPayload);

        verify(validationService)
                .validateNer(taskPayload.getNer());

        assertEquals(actionPayload.getNerSectionsCompleted(),
                taskPayload.getNerSectionsCompleted());

        verify(submitService).submitToRegulator(
                requestTask,
                appUser,
                RequestActionType.NER_APPLICATION_AMENDS_SUBMITTED);
    }

    @Test
    void sendAmendsToVerifier() {
        // given
        NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload actionPayload =
                NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload.builder()
                        .verificationSectionsCompleted(Map.of("verification", List.of(true)))
                        .build();

        NerRequestPayload requestPayload = NerRequestPayload.builder().build();

        NERApplicationAmendsSubmitRequestTaskPayload taskPayload =
                NERApplicationAmendsSubmitRequestTaskPayload.builder()
                        .ner(new NER())
                        .build();

        Request request = Request.builder()
                .payload(requestPayload)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        AppUser appUser = AppUser.builder().build();

        // when
        service.sendAmendsToVerifier(actionPayload, requestTask, appUser);

        // then
        verify(validationService).validateNer(taskPayload.getNer());

        assertEquals(actionPayload.getVerificationSectionsCompleted(),
                requestPayload.getVerificationSectionsCompleted());

        verify(submitService).submitToVerifier(
                actionPayload,
                requestTask,
                appUser,
                RequestActionType.NER_APPLICATION_AMENDS_SENT_TO_VERIFIER);
    }
}
