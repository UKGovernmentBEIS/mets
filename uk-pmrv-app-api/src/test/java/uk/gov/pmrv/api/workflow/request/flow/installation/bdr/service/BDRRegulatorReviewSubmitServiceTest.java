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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRBdrDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRBdrDataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation.BDRValidationService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BDRRegulatorReviewSubmitServiceTest {

    @InjectMocks
    private BDRRegulatorReviewSubmitService submitService;

    @Mock
    private BDRValidationService validationService;

    @Mock
    private RequestService requestService;


    @Test
    void submit() {
        UUID attachmentId = UUID.randomUUID();
        AppUser user = AppUser.builder().build();
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewGroupDecisions(Map.of(BDRReviewGroup.BDR, BDRBdrDataRegulatorReviewDecision.builder().build()))
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .build();

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .verificationPerformed(true)
                .build();

        Request request = Request.builder().type(RequestType.BDR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();

        submitService.submit(requestTask,user);

        verify(validationService, times(1))
                .validateRegulatorReviewGroupDecisions(taskPayload.getRegulatorReviewGroupDecisions(), requestPayload.isVerificationPerformed());

        verify(validationService, times(1))
                .validateRegulatorReviewOutcome(taskPayload.getRegulatorReviewOutcome());


        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(user.getUserId());
        assertThat(requestPayload.getRegulatorReviewGroupDecisions()).isEqualTo(taskPayload.getRegulatorReviewGroupDecisions());
        assertThat(requestPayload.getRegulatorReviewAttachments()).isEqualTo(taskPayload.getRegulatorReviewAttachments());
        assertThat(requestPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskPayload.getRegulatorReviewSectionsCompleted());
    }


    @Test
    void save() {
        UUID attachmentId = UUID.randomUUID();
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewGroupDecisions(Map.of(BDRReviewGroup.BDR, BDRBdrDataRegulatorReviewDecision.builder().build()))
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .build();

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .verificationPerformed(true)
                .build();

        Request request = Request.builder().type(RequestType.BDR).payload(requestPayload).build();


        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();

        BDRApplicationRegulatorReviewSaveTaskActionPayload taskActionPayload = BDRApplicationRegulatorReviewSaveTaskActionPayload
                .builder()
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .regulatorReviewOutcome(BDRApplicationRegulatorReviewOutcome.builder().hasRegulatorSentFreeAllocation(true).build())
                .build();

        submitService.save(taskActionPayload, requestTask);

        assertThat(taskPayload.getRegulatorReviewOutcome()).isEqualTo(taskActionPayload.getRegulatorReviewOutcome());
        assertThat(taskPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskActionPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void saveReviewGroupDecision() {
        BDRReviewGroup group =BDRReviewGroup.BDR;
        BDRBdrDataRegulatorReviewDecision decision = BDRBdrDataRegulatorReviewDecision.builder().build();
        UUID attachmentId = UUID.randomUUID();
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewGroupDecisions(new HashMap<>())
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .build();

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .verificationPerformed(true)
                .build();

        Request request = Request.builder().type(RequestType.BDR).payload(requestPayload).build();


        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();

        BDRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload taskActionPayload = BDRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload
                .builder()
                .group(group)
                .decision(decision)
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .build();

        submitService.saveReviewGroupDecision(taskActionPayload, requestTask);

        assertThat(taskPayload.getRegulatorReviewGroupDecisions()).containsEntry(group,decision);
        assertThat(taskPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskActionPayload.getRegulatorReviewSectionsCompleted());

    }
    
    @Test
    void returnForAmends() {
        AppUser user = AppUser.builder().build();
        BDRReviewGroup group = BDRReviewGroup.BDR;
        BDRBdrDataRegulatorReviewDecision decision = BDRBdrDataRegulatorReviewDecision
                .builder()
                .reviewDataType(BDRReviewDataType.BDR_DATA)
                .type(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails
                        .builder()
                        .build())
                .build();
        UUID attachmentId = UUID.randomUUID();
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewGroupDecisions(Map.of(group,decision))
                .build();

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .verificationPerformed(true)
                .build();

        Request request = Request.builder().type(RequestType.BDR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();


        BDRRegulatorReviewReturnedForAmendsRequestActionPayload requestActionPayload = BDRRegulatorReviewReturnedForAmendsRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.BDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD)
                .regulatorReviewGroupDecisions(Map.of(group,decision))
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .build();


        submitService.returnForAmends(requestTask,user);

        verify(validationService, times(1)).validateReturnForAmends(taskPayload);
        verify(requestService, times(1))
                .addActionToRequest(request, requestActionPayload, RequestActionType.BDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS, user.getUserId());


        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(user.getUserId());
        assertThat(requestPayload.getRegulatorReviewGroupDecisions()).isEqualTo(taskPayload.getRegulatorReviewGroupDecisions());
        assertThat(requestPayload.getRegulatorReviewAttachments()).isEqualTo(taskPayload.getRegulatorReviewAttachments());
        assertThat(requestPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskPayload.getRegulatorReviewSectionsCompleted());

    }

    @Test
    void updateRequestPayload() {
        UUID attachmentId = UUID.randomUUID();
        AppUser user = AppUser.builder().build();
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewGroupDecisions(Map.of(BDRReviewGroup.BDR, BDRBdrDataRegulatorReviewDecision.builder().build()))
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .regulatorReviewOutcome(BDRApplicationRegulatorReviewOutcome.builder().useHseNotes("test").build())
                .build();

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .verificationPerformed(true)
                .build();

        Request request = Request.builder().type(RequestType.BDR).payload(requestPayload).build();

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
