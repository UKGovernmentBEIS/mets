package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2SaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Files;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation.BDRS2ValidationService;

import java.util.List;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BDRS2RegulatorReviewSubmitServiceTest {

    @InjectMocks
    private BDRS2RegulatorReviewSubmitService service;

    @Mock
    private BDRS2ValidationService validationService;

    @Mock
    private RequestService requestService;

    @Test
    void submit() {
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();

        final Map<BDRS2ReviewGroup, BDRS2ReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(BDRS2ReviewGroup.class);
        regulatorReviewGroupDecisions.put(
                BDRS2ReviewGroup.BDRS2,
                BDRS2Bdrs2DataRegulatorReviewDecision.builder()
                        .reviewDataType(BDRS2ReviewDataType.BDRS2_DATA)
                        .build()
        );

        final BDRS2ApplicationRegulatorReviewOutcome regulatorReviewOutcome = BDRS2ApplicationRegulatorReviewOutcome.builder().build();
        final Map<String, Boolean> reviewSectionsCompleted = Map.of("section1", true);
        final Map<UUID, String> reviewAttachments = new HashMap<>();
        final UUID attachmentUuid = UUID.randomUUID();
        reviewAttachments.put(attachmentUuid, "attachment.pdf");

        final BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewGroupDecisions(regulatorReviewGroupDecisions)
                        .regulatorReviewOutcome(regulatorReviewOutcome)
                        .regulatorReviewSectionsCompleted(reviewSectionsCompleted)
                        .regulatorReviewAttachments(reviewAttachments)
                        .build();

        final BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder()
                .bdrs2(BDRS2.builder().bdrs2Files(BDRS2Files.builder().file(UUID.randomUUID()).build()).build())
                .verificationPerformed(false)
                .build();

        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        service.submit(requestTask, appUser);

        verify(validationService, times(1))
                .validateRegulatorReviewGroupDecisions(regulatorReviewGroupDecisions, false);

        assertEquals(userId, requestPayload.getRegulatorReviewer());
        assertEquals(regulatorReviewOutcome, requestPayload.getRegulatorReviewOutcome());
        assertEquals(regulatorReviewGroupDecisions, requestPayload.getRegulatorReviewGroupDecisions());
        assertEquals(reviewAttachments, requestPayload.getRegulatorReviewAttachments());
        assertEquals(reviewSectionsCompleted, requestPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void save() {
        final Map<String, Boolean> reviewSectionsCompleted = Map.of("section1", true, "section2", false);
        final BDRS2ApplicationRegulatorReviewOutcome regulatorReviewOutcome = BDRS2ApplicationRegulatorReviewOutcome.builder().build();

        final BDRS2ApplicationRegulatorReviewSaveTaskActionPayload payload =
                BDRS2ApplicationRegulatorReviewSaveTaskActionPayload.builder()
                        .regulatorReviewOutcome(regulatorReviewOutcome)
                        .regulatorReviewSectionsCompleted(reviewSectionsCompleted)
                        .build();

        final BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload.builder().build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        service.save(payload, requestTask);

        assertEquals(regulatorReviewOutcome, taskPayload.getRegulatorReviewOutcome());
        assertEquals(reviewSectionsCompleted, taskPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void saveReviewGroupDecision() {
        final BDRS2ReviewGroup group = BDRS2ReviewGroup.BDRS2;
        final BDRS2ReviewDecision decision = BDRS2Bdrs2DataRegulatorReviewDecision.builder()
                .reviewDataType(BDRS2ReviewDataType.BDRS2_DATA)
                .build();
        final Map<String, Boolean> reviewSectionsCompleted = Map.of("section1", true);

        final BDRS2SaveRegulatorReviewGroupDecisionRequestTaskActionPayload payload =
                BDRS2SaveRegulatorReviewGroupDecisionRequestTaskActionPayload.builder()
                        .group(group)
                        .decision(decision)
                        .regulatorReviewSectionsCompleted(reviewSectionsCompleted)
                        .build();

        final Map<BDRS2ReviewGroup, BDRS2ReviewDecision> existingDecisions = new EnumMap<>(BDRS2ReviewGroup.class);
        final BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewGroupDecisions(existingDecisions)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        service.saveReviewGroupDecision(payload, requestTask);

        assertEquals(decision, taskPayload.getRegulatorReviewGroupDecisions().get(group));
        assertEquals(reviewSectionsCompleted, taskPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void updateRequestPayload() {
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();

        final Map<BDRS2ReviewGroup, BDRS2ReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(BDRS2ReviewGroup.class);
        final BDRS2ApplicationRegulatorReviewOutcome regulatorReviewOutcome = BDRS2ApplicationRegulatorReviewOutcome.builder().build();
        final Map<String, Boolean> reviewSectionsCompleted = Map.of("section1", true);
        final Map<UUID, String> reviewAttachments = new HashMap<>();

        final BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewGroupDecisions(regulatorReviewGroupDecisions)
                        .regulatorReviewOutcome(regulatorReviewOutcome)
                        .regulatorReviewSectionsCompleted(reviewSectionsCompleted)
                        .regulatorReviewAttachments(reviewAttachments)
                        .build();

        final BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        service.updateRequestPayload(requestTask, appUser);

        assertEquals(userId, requestPayload.getRegulatorReviewer());
        assertEquals(regulatorReviewOutcome, requestPayload.getRegulatorReviewOutcome());
        assertEquals(regulatorReviewGroupDecisions, requestPayload.getRegulatorReviewGroupDecisions());
        assertEquals(reviewAttachments, requestPayload.getRegulatorReviewAttachments());
        assertEquals(reviewSectionsCompleted, requestPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void returnForAmends() {
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();

        final BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails decisionDetails =
                BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                        .verificationRequired(false)
                        .requiredChanges(List.of(
                                BDRS2Bdrs2DataRegulatorReviewRequiredChange.builder()
                                        .reason("Test reason")
                                        .build()
                        ))
                        .build();

        final Map<BDRS2ReviewGroup, BDRS2ReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(BDRS2ReviewGroup.class);
        regulatorReviewGroupDecisions.put(
                BDRS2ReviewGroup.BDRS2,
                BDRS2Bdrs2DataRegulatorReviewDecision.builder()
                        .reviewDataType(BDRS2ReviewDataType.BDRS2_DATA)
                        .type(BDRS2Bdrs2DataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .details(decisionDetails)
                        .build()
        );

        final Map<UUID, String> reviewAttachments = new HashMap<>();
        final UUID attachmentUuid = UUID.randomUUID();
        reviewAttachments.put(attachmentUuid, "attachment.pdf");

        final BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewGroupDecisions(regulatorReviewGroupDecisions)
                        .regulatorReviewAttachments(reviewAttachments)
                        .build();

        final BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder().build();
        final Request request = Request.builder()
                .id("requestId")
                .payload(requestPayload)
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        service.returnForAmends(requestTask, appUser);

        verify(validationService, times(1))
                .validateReturnForAmends(taskPayload);

        verify(requestService, times(1))
                .addActionToRequest(
                        eq(request),
                        any(),
                        eq(RequestActionType.BDRS2_REGULATOR_REVIEW_RETURNED_FOR_AMENDS),
                        eq(userId)
                );

        assertEquals(userId, requestPayload.getRegulatorReviewer());
        assertEquals(regulatorReviewGroupDecisions, requestPayload.getRegulatorReviewGroupDecisions());
        assertEquals(reviewAttachments, requestPayload.getRegulatorReviewAttachments());
    }
}
