package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation.BDRValidationService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRAmendsSubmitServiceTest {

    @InjectMocks
    private BDRAmendsSubmitService submitService;

    @Mock
    private BDRSubmitService bdrSubmitService;

    @Mock
    private BDRValidationService validationService;

    @Test
    public void saveAmends() {
        BDRApplicationAmendsSaveRequestTaskActionPayload taskActionPayload = BDRApplicationAmendsSaveRequestTaskActionPayload
                .builder()
                .bdr(BDR.builder().build())
                .bdrSectionsCompleted(Map.of("test",true))
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .build();

        BDRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =   BDRApplicationAmendsSubmitRequestTaskPayload
                  .builder()
                  .build();

        RequestTask requestTask = RequestTask
                  .builder()
                  .type(RequestTaskType.BDR_APPLICATION_AMENDS_SUBMIT)
                  .payload(requestTaskPayload)
                  .build();

        submitService.saveAmends(taskActionPayload, requestTask);

        assertThat(requestTaskPayload.getBdr()).isEqualTo(taskActionPayload.getBdr());

        assertThat(requestTaskPayload.getBdrSectionsCompleted()).isEqualTo(taskActionPayload.getBdrSectionsCompleted());
        assertThat(requestTaskPayload.getRegulatorReviewSectionsCompleted())
                .isEqualTo(taskActionPayload.getRegulatorReviewSectionsCompleted());
        assertThat(requestTaskPayload.isVerificationPerformed()).isFalse();

        //verify(bdrValidationService, times(1)).validateBDR(bdr);
    }

    @Test
    public void submitToRegulator() {

        final AppUser user = AppUser.builder().userId("user").build();

        BDRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =   BDRApplicationAmendsSubmitRequestTaskPayload
                  .builder()
                  .build();

         final BDRApplicationAmendsSubmitRequestTaskActionPayload payload = BDRApplicationAmendsSubmitRequestTaskActionPayload
                .builder()
                .payloadType(RequestTaskActionPayloadType.BDR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR_PAYLOAD)
                 .bdrSectionsCompleted(Map.of("test",true))
                 .build();

        BDRRequestPayload requestPayload = BDRRequestPayload.builder().build();
        Request request = Request.builder().type(RequestType.BDR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                  .builder()
                  .type(RequestTaskType.BDR_APPLICATION_AMENDS_SUBMIT)
                  .payload(requestTaskPayload)
                  .request(request)
                  .build();

        submitService.submitToRegulator(payload, requestTask,user);

        verify(validationService, times(1)).validateAmendsVerification(requestPayload,requestTaskPayload);
        verify(bdrSubmitService, times(1)).submitToRegulator(requestTask,user);

        assertThat(( (BDRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload()).getBdrSectionsCompleted())
                .containsExactlyEntriesOf(payload.getBdrSectionsCompleted());
    }

    @Test
    public void sendAmendsToVerifier() {

        final AppUser user = AppUser.builder().userId("user").build();

        BDRApplicationAmendsSubmitToVerifierRequestTaskActionPayload taskActionPayload = BDRApplicationAmendsSubmitToVerifierRequestTaskActionPayload
                .builder()
                .verificationSectionsCompleted(Map.of("test", List.of(true)))
                .build();

        BDRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =   BDRApplicationAmendsSubmitRequestTaskPayload
                  .builder()
                  .regulatorReviewSectionsCompleted(Map.of("test",true))
                   .bdr(BDR.builder().build())
                  .build();

        BDRRequestPayload requestPayload = BDRRequestPayload.builder().build();
        Request request = Request.builder().type(RequestType.BDR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                  .builder()
                  .type(RequestTaskType.BDR_APPLICATION_AMENDS_SUBMIT)
                  .payload(requestTaskPayload)
                  .request(request)
                  .build();


        BDRApplicationSubmittedRequestActionPayload requestActionPayload = BDRApplicationSubmittedRequestActionPayload
                .builder()
                .bdr(requestPayload.getBdr())
                .build();

        when(bdrSubmitService.createApplicationSubmittedRequestActionPayload(requestTask, requestTaskPayload, requestPayload, RequestActionPayloadType.BDR_APPLICATION_AMENDS_SUBMITTED_PAYLOAD))
                .thenReturn(requestActionPayload);

        submitService.sendAmendsToVerifier(taskActionPayload,requestTask,user);

        assertThat(requestPayload.getRegulatorReviewSectionsCompleted())
                .isEqualTo(requestTaskPayload.getRegulatorReviewSectionsCompleted());

        assertThat(requestPayload.getVerificationSectionsCompleted())
                .isEqualTo(taskActionPayload.getVerificationSectionsCompleted());

        verify(validationService, times(1)).validateBDR(requestTaskPayload.getBdr());
        verify(bdrSubmitService, times(1)).createApplicationSubmittedRequestActionPayload(requestTask, requestTaskPayload, requestPayload, RequestActionPayloadType.BDR_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);
        verify(bdrSubmitService, times(1)).submitBDR(requestPayload,requestTask, user, RequestActionType.BDR_APPLICATION_AMENDS_SENT_TO_VERIFIER, requestActionPayload, requestTaskPayload.getBdrSectionsCompleted());

    }

}
