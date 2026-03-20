package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;


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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation.BDRS2ValidationService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2AmendsSubmitServiceTest {

    @InjectMocks
    private BDRS2AmendsSubmitService submitService;

    @Mock
    private BDRS2SubmitService bdrs2SubmitService;

    @Mock
    private BDRS2ValidationService validationService;

    @Test
    void saveAmends() {
        BDRS2ApplicationAmendsSaveRequestTaskActionPayload taskActionPayload = BDRS2ApplicationAmendsSaveRequestTaskActionPayload
                .builder()
                .bdrs2(BDRS2.builder().build())
                .bdrs2SectionsCompleted(Map.of("test", true))
                .regulatorReviewSectionsCompleted(Map.of("test", true))
                .build();

        BDRS2ApplicationAmendsSubmitRequestTaskPayload requestTaskPayload = BDRS2ApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .build();

        RequestTask requestTask = RequestTask
                .builder()
                .type(RequestTaskType.BDRS2_APPLICATION_AMENDS_SUBMIT)
                .payload(requestTaskPayload)
                .build();

        submitService.saveAmends(taskActionPayload, requestTask);

        assertThat(requestTaskPayload.getBdrs2()).isEqualTo(taskActionPayload.getBdrs2());
        assertThat(requestTaskPayload.getBdrs2SectionsCompleted()).isEqualTo(taskActionPayload.getBdrs2SectionsCompleted());
        assertThat(requestTaskPayload.getRegulatorReviewSectionsCompleted())
                .isEqualTo(taskActionPayload.getRegulatorReviewSectionsCompleted());
        assertThat(requestTaskPayload.isVerificationPerformed()).isFalse();
    }

    @Test
    void submitToRegulator() {
        final AppUser user = AppUser.builder().userId("user").build();

        BDRS2ApplicationAmendsSubmitRequestTaskPayload requestTaskPayload = BDRS2ApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .build();

        final BDRS2ApplicationAmendsSubmitRequestTaskActionPayload payload = BDRS2ApplicationAmendsSubmitRequestTaskActionPayload
                .builder()
                .payloadType(RequestTaskActionPayloadType.BDRS2_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR_PAYLOAD)
                .bdrs2SectionsCompleted(Map.of("bdrs2_section_1", true))
                .regulatorReviewSectionsCompleted(Map.of("reg_review_section_1", false))
                .build();

        BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder().build();
        Request request = Request.builder().type(RequestType.BDRS2).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .type(RequestTaskType.BDRS2_APPLICATION_AMENDS_SUBMIT)
                .payload(requestTaskPayload)
                .request(request)
                .build();

        submitService.submitToRegulator(payload, requestTask, user);

        verify(validationService, times(1)).validateAmendsVerification(requestPayload, requestTaskPayload);
        verify(bdrs2SubmitService, times(1)).submitToRegulator(requestTask, user);

        assertThat(((BDRS2ApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload()).getBdrs2SectionsCompleted())
                .containsExactlyEntriesOf(payload.getBdrs2SectionsCompleted());
        assertThat(((BDRS2ApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload()).getRegulatorReviewSectionsCompleted())
                .containsExactlyEntriesOf(payload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void sendAmendsToVerifier() {
        final AppUser user = AppUser.builder().userId("user").build();

        BDRS2ApplicationAmendsSubmitToVerifierRequestTaskActionPayload taskActionPayload = BDRS2ApplicationAmendsSubmitToVerifierRequestTaskActionPayload
                .builder()
                .verificationSectionsCompleted(Map.of("test", List.of(true)))
                .build();

        BDRS2ApplicationAmendsSubmitRequestTaskPayload requestTaskPayload = BDRS2ApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .regulatorReviewSectionsCompleted(Map.of("test", true))
                .bdrs2(BDRS2.builder().build())
                .build();

        BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder().build();
        Request request = Request.builder().type(RequestType.BDRS2).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .type(RequestTaskType.BDRS2_APPLICATION_AMENDS_SUBMIT)
                .payload(requestTaskPayload)
                .request(request)
                .build();

        BDRS2ApplicationSubmittedRequestActionPayload requestActionPayload = BDRS2ApplicationSubmittedRequestActionPayload
                .builder()
                .bdrs2(requestPayload.getBdrs2())
                .build();

        when(bdrs2SubmitService.createApplicationSubmittedRequestActionPayload(requestTask, requestTaskPayload, requestPayload, RequestActionPayloadType.BDRS2_APPLICATION_AMENDS_SUBMITTED_PAYLOAD))
                .thenReturn(requestActionPayload);

        submitService.sendAmendsToVerifier(taskActionPayload, requestTask, user);

        assertThat(requestPayload.getRegulatorReviewSectionsCompleted())
                .isEqualTo(requestTaskPayload.getRegulatorReviewSectionsCompleted());

        assertThat(requestPayload.getVerificationSectionsCompleted())
                .isEqualTo(taskActionPayload.getVerificationSectionsCompleted());

        verify(validationService, times(1)).validateBDRS2(requestTaskPayload.getBdrs2());
        verify(bdrs2SubmitService, times(1)).createApplicationSubmittedRequestActionPayload(requestTask, requestTaskPayload, requestPayload, RequestActionPayloadType.BDRS2_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);
        verify(bdrs2SubmitService, times(1)).submitBDRS2(requestPayload, requestTask, user, RequestActionType.BDRS2_APPLICATION_AMENDS_SENT_TO_VERIFIER, requestActionPayload, requestTaskPayload.getBdrs2SectionsCompleted());
    }
}
