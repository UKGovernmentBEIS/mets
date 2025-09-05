package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAlrDataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAlrDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRValidationService;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ALRRegulatorReviewSubmitServiceTest {

    @InjectMocks
    private ALRRegulatorReviewSubmitService submitService;

    @Mock
    private ALRValidationService validationService;

    @Mock
    private RequestService requestService;

    @Test
    void save() {
        UUID attachmentId = UUID.randomUUID();
        ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewGroupDecisions(Map.of(ALRReviewGroup.ALR, ALRAlrDataRegulatorReviewDecision.builder().build()))
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .build();

        ALRRequestPayload requestPayload = ALRRequestPayload
                .builder()
                .verificationPerformed(true)
                .build();

        Request request = Request.builder().type(RequestType.ALR).payload(requestPayload).build();


        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();

        ALRApplicationRegulatorReviewSaveTaskActionPayload taskActionPayload = ALRApplicationRegulatorReviewSaveTaskActionPayload
                .builder()
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().build())
                .build();

        submitService.save(taskActionPayload, requestTask);

        assertThat(taskPayload.getRegulatorReviewOutcome()).isEqualTo(taskActionPayload.getRegulatorReviewOutcome());
        assertThat(taskPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskActionPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void saveReviewGroupDecision() {
        ALRReviewGroup group = ALRReviewGroup.ALR;
        ALRAlrDataRegulatorReviewDecision decision = ALRAlrDataRegulatorReviewDecision.builder().build();
        UUID attachmentId = UUID.randomUUID();
        ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewGroupDecisions(new HashMap<>())
                .regulatorReviewAttachments(Map.of(attachmentId, "test"))
                .build();

        ALRRequestPayload requestPayload = ALRRequestPayload
                .builder()
                .verificationPerformed(true)
                .build();

        Request request = Request.builder().type(RequestType.ALR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();

        ALRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload taskActionPayload = ALRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload
                .builder()
                .group(group)
                .decision(decision)
                .regulatorReviewSectionsCompleted(Map.of("test", true))
                .build();

        submitService.saveReviewGroupDecision(taskActionPayload, requestTask);

        assertThat(taskPayload.getRegulatorReviewGroupDecisions()).containsEntry(group, decision);
        assertThat(taskPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskActionPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void returnForAmends() {
        AppUser user = AppUser.builder().build();
        ALRReviewGroup group = ALRReviewGroup.ALR;
        ALRAlrDataRegulatorReviewDecision decision = ALRAlrDataRegulatorReviewDecision
                .builder()
                .reviewDataType(ALRReviewDataType.ALR_DATA)
                .type(ALRAlrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails
                        .builder()
                        .build())
                .build();
        UUID attachmentId = UUID.randomUUID();
        ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewGroupDecisions(Map.of(group,decision))
                .build();

        ALRRequestPayload requestPayload = ALRRequestPayload
                .builder()
                .verificationPerformed(true)
                .build();

        Request request = Request.builder().type(RequestType.ALR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();


        ALRRegulatorReviewReturnedForAmendsRequestActionPayload requestActionPayload = ALRRegulatorReviewReturnedForAmendsRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.ALR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD)
                .regulatorReviewGroupDecisions(Map.of(group,decision))
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .build();


        submitService.returnForAmends(requestTask,user);

        verify(validationService, times(1)).validateReturnForAmends(taskPayload);
        verify(requestService, times(1))
                .addActionToRequest(request, requestActionPayload, RequestActionType.ALR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS, user.getUserId());


        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(user.getUserId());
        assertThat(requestPayload.getRegulatorReviewGroupDecisions()).isEqualTo(taskPayload.getRegulatorReviewGroupDecisions());
        assertThat(requestPayload.getRegulatorReviewAttachments()).isEqualTo(taskPayload.getRegulatorReviewAttachments());
        assertThat(requestPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskPayload.getRegulatorReviewSectionsCompleted());

    }

    @Test
    void updateRequestPayload() {
        UUID attachmentId = UUID.randomUUID();
        AppUser user = AppUser.builder().build();
        ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewGroupDecisions(Map.of(ALRReviewGroup.ALR, ALRAlrDataRegulatorReviewDecision.builder().build()))
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().build())
                .build();

        ALRRequestPayload requestPayload = ALRRequestPayload
                .builder()
                .verificationPerformed(true)
                .build();

        Request request = Request.builder().type(RequestType.ALR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();

        submitService.updateRequestPayload(requestTask,user);

        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(user.getUserId());
        assertThat(requestPayload.getRegulatorReviewGroupDecisions()).isEqualTo(taskPayload.getRegulatorReviewGroupDecisions());
        assertThat(requestPayload.getRegulatorReviewOutcome()).isEqualTo(taskPayload.getRegulatorReviewOutcome());
        assertThat(requestPayload.getRegulatorReviewAttachments()).isEqualTo(taskPayload.getRegulatorReviewAttachments());
        assertThat(requestPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskPayload.getRegulatorReviewSectionsCompleted());
    }
}
