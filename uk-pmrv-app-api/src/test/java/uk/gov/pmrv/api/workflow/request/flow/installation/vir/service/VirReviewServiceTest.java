package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

import java.time.Year;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirReviewServiceTest {

    @InjectMocks
    private VirReviewService virReviewService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void saveReview() {
        final Map<String, Boolean> reviewSectionsCompleted = Map.of("A1", true);
        final RegulatorReviewResponse regulatorReviewResponse = RegulatorReviewResponse.builder()
                .regulatorImprovementResponses(Map.of("A1", RegulatorImprovementResponse.builder().improvementRequired(false).build()))
                .reportSummary("Report summary")
                .build();

        final VirSaveReviewRequestTaskActionPayload actionPayload =
                VirSaveReviewRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.VIR_SAVE_REVIEW_PAYLOAD)
                        .regulatorReviewResponse(regulatorReviewResponse)
                        .reviewSectionsCompleted(reviewSectionsCompleted)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(VirApplicationReviewRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.VIR_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of("A2", true))
                        .build())
                .build();

        // Invoke
        virReviewService.saveReview(actionPayload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isInstanceOf(VirApplicationReviewRequestTaskPayload.class);

        final VirApplicationReviewRequestTaskPayload payloadSaved =
                (VirApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(payloadSaved.getRegulatorReviewResponse()).isEqualTo(regulatorReviewResponse);
        assertThat(payloadSaved.getReviewSectionsCompleted()).isEqualTo(reviewSectionsCompleted);
    }

    @Test
    void submitReview() {
        final String userId = "userId";
        final PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();

        final Map<String, Boolean> reviewSectionsCompleted = Map.of("A1", true);
        final RegulatorReviewResponse regulatorReviewResponse = RegulatorReviewResponse.builder()
                .regulatorImprovementResponses(Map.of("A1", RegulatorImprovementResponse.builder().build()))
                .reportSummary("Report summary")
                .build();

        Request request = Request.builder()
                .payload(VirRequestPayload.builder().payloadType(RequestPayloadType.VIR_REQUEST_PAYLOAD).build())
                .build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(VirApplicationReviewRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.VIR_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(reviewSectionsCompleted)
                        .regulatorReviewResponse(regulatorReviewResponse)
                        .build())
                .build();

        // Invoke
        virReviewService.submitReview(requestTask, decisionNotification, pmrvUser);

        // Verify
        assertThat(request.getPayload()).isInstanceOf(VirRequestPayload.class);

        final VirRequestPayload payloadSaved = (VirRequestPayload) request.getPayload();

        assertThat(payloadSaved.getRegulatorReviewResponse()).isEqualTo(regulatorReviewResponse);
        assertThat(payloadSaved.getReviewSectionsCompleted()).isEqualTo(reviewSectionsCompleted);
        assertThat(payloadSaved.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(payloadSaved.getRegulatorReviewer()).isEqualTo(userId);
    }

    @Test
    void addReviewedRequestAction() {
        final String requestId = "1";
        final String reviewer = "regUser";
        final VirVerificationData verificationData = VirVerificationData.builder()
                .uncorrectedNonConformities(Map.of("A1", UncorrectedItem.builder().build()))
                .build();
        final Map<String, OperatorImprovementResponse> operatorImprovementResponses = Map.of(
                "A1", OperatorImprovementResponse.builder().build()
        );
        final RegulatorReviewResponse regulatorReviewResponse = RegulatorReviewResponse.builder()
                .reportSummary("Report Summary")
                .regulatorImprovementResponses(Map.of("A1", RegulatorImprovementResponse.builder().build()))
                .build();
        final Map<UUID, String> virAttachments = Map.of(UUID.randomUUID(), "file");
        final Year reportingYear = Year.now();

        final Set<String> operators = Set.of("op1", "op2");
        final String signatory = "reg";
        final Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "op1", RequestActionUserInfo.builder().name("operator1").roleCode("admin").build(),
                "op2", RequestActionUserInfo.builder().name("operator2").roleCode("admin").build(),
                "reg", RequestActionUserInfo.builder().name("regulator").roleCode("admin").build()
        );
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(operators)
                .signatory(signatory)
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .metadata(VirRequestMetadata.builder()
                        .year(reportingYear)
                        .build())
                .payload(VirRequestPayload.builder()
                        .payloadType(RequestPayloadType.VIR_REQUEST_PAYLOAD)
                        .verificationData(verificationData)
                        .operatorImprovementResponses(operatorImprovementResponses)
                        .regulatorReviewResponse(regulatorReviewResponse)
                        .virAttachments(virAttachments)
                        .regulatorReviewer(reviewer)
                        .decisionNotification(decisionNotification)
                        .build())
                .build();

        final VirApplicationReviewedRequestActionPayload actionPayload = VirApplicationReviewedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.VIR_APPLICATION_REVIEWED_PAYLOAD)
                .reportingYear(reportingYear)
                .verificationData(verificationData)
                .operatorImprovementResponses(operatorImprovementResponses)
                .regulatorReviewResponse(regulatorReviewResponse)
                .virAttachments(virAttachments)
                .decisionNotification(decisionNotification)
                .usersInfo(usersInfo)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(operators, signatory, request))
                .thenReturn(usersInfo);

        // Invoke
        virReviewService.addReviewedRequestAction(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(request,
                actionPayload, RequestActionType.VIR_APPLICATION_REVIEWED, reviewer);
    }
}
