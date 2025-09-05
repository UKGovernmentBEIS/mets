package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRValidationService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ALRAmendsSubmitServiceTest {

    @InjectMocks
    private ALRAmendsSubmitService submitService;

    @Mock
    private ALRSubmitService alrSubmitService;

    @Mock
    private ALRValidationService validationService;

    @Test
    public void saveAmends() {
        ALRApplicationAmendsSaveRequestTaskActionPayload taskActionPayload = ALRApplicationAmendsSaveRequestTaskActionPayload
                .builder()
                .alr(ALR.builder().build())
                .alrSectionsCompleted(Map.of("test",true))
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .build();

        ALRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =   ALRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .build();

        RequestTask requestTask = RequestTask
                .builder()
                .type(RequestTaskType.ALR_APPLICATION_AMENDS_SUBMIT)
                .payload(requestTaskPayload)
                .build();

        submitService.saveAmends(taskActionPayload, requestTask);

        assertThat(requestTaskPayload.getAlr()).isEqualTo(taskActionPayload.getAlr());

        assertThat(requestTaskPayload.getAlrSectionsCompleted()).isEqualTo(taskActionPayload.getAlrSectionsCompleted());
        assertThat(requestTaskPayload.getRegulatorReviewSectionsCompleted())
                .isEqualTo(taskActionPayload.getRegulatorReviewSectionsCompleted());
        assertThat(requestTaskPayload.isVerificationPerformed()).isFalse();

    }

    @Test
    public void submitToRegulator() {

        final AppUser user = AppUser.builder().userId("user").build();

        ALRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =   ALRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .build();

        final ALRApplicationAmendsSubmitRequestTaskActionPayload payload = ALRApplicationAmendsSubmitRequestTaskActionPayload
                .builder()
                .payloadType(RequestTaskActionPayloadType.ALR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR_PAYLOAD)
                .alrSectionsCompleted(Map.of("test",true))
                .build();

        ALRRequestPayload requestPayload = ALRRequestPayload.builder().build();
        Request request = Request.builder().type(RequestType.ALR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .type(RequestTaskType.ALR_APPLICATION_AMENDS_SUBMIT)
                .payload(requestTaskPayload)
                .request(request)
                .build();

        submitService.submitToRegulator(payload, requestTask,user);

        verify(validationService, times(1)).validateAmendsVerification(requestPayload,requestTaskPayload);

        assertThat(( (ALRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload()).getAlrSectionsCompleted())
                .containsExactlyEntriesOf(payload.getAlrSectionsCompleted());
    }

    @Test
    public void sendAmendsToVerifier() {

        final AppUser user = AppUser.builder().userId("user").build();

        ALRApplicationAmendsSubmitToVerifierRequestTaskActionPayload taskActionPayload = ALRApplicationAmendsSubmitToVerifierRequestTaskActionPayload
                .builder()
                .verificationSectionsCompleted(Map.of("test", List.of(true)))
                .build();

        ALRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =   ALRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .alr(ALR.builder().build())
                .build();

        ALRRequestPayload requestPayload = ALRRequestPayload.builder().build();
        Request request = Request.builder().type(RequestType.ALR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .type(RequestTaskType.ALR_APPLICATION_AMENDS_SUBMIT)
                .payload(requestTaskPayload)
                .request(request)
                .build();


        ALRApplicationSubmittedRequestActionPayload requestActionPayload = ALRApplicationSubmittedRequestActionPayload
                .builder()
                .alr(requestPayload.getAlr())
                .build();

        when(alrSubmitService.createApplicationSubmittedRequestActionPayload(requestTask, requestTaskPayload, requestPayload, RequestActionPayloadType.ALR_APPLICATION_AMENDS_SUBMITTED_PAYLOAD))
                .thenReturn(requestActionPayload);

        submitService.sendAmendsToVerifier(taskActionPayload,requestTask,user);

        assertThat(requestPayload.getRegulatorReviewSectionsCompleted())
                .isEqualTo(requestTaskPayload.getRegulatorReviewSectionsCompleted());

        assertThat(requestPayload.getVerificationSectionsCompleted())
                .isEqualTo(taskActionPayload.getVerificationSectionsCompleted());

        verify(validationService, times(1)).validateALR(requestTaskPayload.getAlr());
        verify(alrSubmitService, times(1)).createApplicationSubmittedRequestActionPayload(requestTask, requestTaskPayload, requestPayload, RequestActionPayloadType.ALR_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);
        verify(alrSubmitService, times(1)).submitALR(requestPayload,requestTask, user, RequestActionType.ALR_APPLICATION_AMENDS_SENT_TO_VERIFIER, requestActionPayload, requestTaskPayload.getAlrSectionsCompleted());

    }
}
