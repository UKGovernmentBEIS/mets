package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSaveReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementYesResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirReviewResponse;

@ExtendWith(MockitoExtension.class)
class AirReviewServiceTest {

    @InjectMocks
    private AirReviewService reviewService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void saveReview() {
        
        final Map<String, Boolean> reviewSectionsCompleted = Map.of("1", true);
        final Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses = Map.of(
            1, RegulatorAirImprovementResponse.builder().improvementRequired(true).build()
        );
        final AirSaveReviewRequestTaskActionPayload actionPayload =
            AirSaveReviewRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AIR_SAVE_REVIEW_PAYLOAD)
                        .regulatorReviewResponse(RegulatorAirReviewResponse.builder().regulatorImprovementResponses(regulatorImprovementResponses).build())
                        .reviewSectionsCompleted(reviewSectionsCompleted)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(AirApplicationReviewRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AIR_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of("1", true))
                        .build())
                .build();

        // Invoke
        reviewService.saveReview(actionPayload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isInstanceOf(AirApplicationReviewRequestTaskPayload.class);

        final AirApplicationReviewRequestTaskPayload payloadSaved =
                (AirApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(payloadSaved.getRegulatorReviewResponse().getRegulatorImprovementResponses()).isEqualTo(regulatorImprovementResponses);
        assertThat(payloadSaved.getReviewSectionsCompleted()).isEqualTo(reviewSectionsCompleted);
    }

    @Test
    void submitReview() {
        
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();

        final Map<String, Boolean> reviewSectionsCompleted = Map.of("1", true);
        final Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses = Map.of(
            1, RegulatorAirImprovementResponse.builder().improvementRequired(true).build()
        );

        final Request request = Request.builder()
                .payload(AirRequestPayload.builder().payloadType(RequestPayloadType.AIR_REQUEST_PAYLOAD).build())
                .build();
        final String reportSummary = "reportSummary";
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(AirApplicationReviewRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AIR_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(reviewSectionsCompleted)
                    .regulatorReviewResponse(RegulatorAirReviewResponse.builder().regulatorImprovementResponses(regulatorImprovementResponses)
                        .reportSummary(reportSummary).build())
                        .build())
                .build();

        // Invoke
        reviewService.submitReview(requestTask, decisionNotification, appUser);

        // Verify
        assertThat(request.getPayload()).isInstanceOf(AirRequestPayload.class);

        final AirRequestPayload payloadSaved = (AirRequestPayload) request.getPayload();

        assertThat(payloadSaved.getRegulatorReviewResponse().getRegulatorImprovementResponses()).isEqualTo(regulatorImprovementResponses);
        assertThat(payloadSaved.getReviewSectionsCompleted()).isEqualTo(reviewSectionsCompleted);
        assertThat(payloadSaved.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(payloadSaved.getRegulatorReviewResponse().getReportSummary()).isEqualTo(reportSummary);
        assertThat(payloadSaved.getRegulatorReviewer()).isEqualTo(userId);
    }

    @Test
    void addReviewedRequestAction() {
        
        final String requestId = "1";
        final String reviewer = "regUser";

        final Map<Integer, RegulatorAirImprovementResponse> regulatorAirImprovementResponses = Map.of(
            1, RegulatorAirImprovementResponse.builder().improvementRequired(true).build()
        );
        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses = Map.of(
            1, OperatorAirImprovementYesResponse.builder().build()
        );
        
        final Map<UUID, String> airAttachments = Map.of(UUID.randomUUID(), "file");
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

        final String reportSummary = "report summary";
        final Request request = Request.builder()
                .id(requestId)
                .metadata(AirRequestMetadata.builder()
                        .year(reportingYear)
                        .build())
                .payload(AirRequestPayload.builder()
                        .payloadType(RequestPayloadType.AIR_REQUEST_PAYLOAD)
                        .operatorImprovementResponses(operatorImprovementResponses)
                    .regulatorReviewResponse(RegulatorAirReviewResponse.builder().regulatorImprovementResponses(regulatorAirImprovementResponses)
                        .reportSummary(reportSummary).build())
                        .airAttachments(airAttachments)
                        .regulatorReviewer(reviewer)
                        .decisionNotification(decisionNotification)
                        .build())
                .build();

        final AirApplicationReviewedRequestActionPayload actionPayload = AirApplicationReviewedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.AIR_APPLICATION_REVIEWED_PAYLOAD)
                .reportingYear(reportingYear)
                .operatorImprovementResponses(operatorImprovementResponses)
            .regulatorReviewResponse(RegulatorAirReviewResponse.builder().regulatorImprovementResponses(regulatorAirImprovementResponses)
                .reportSummary(reportSummary).build())
                .airAttachments(airAttachments)
                .decisionNotification(decisionNotification)
                .usersInfo(usersInfo)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(operators, signatory, request))
                .thenReturn(usersInfo);

        // Invoke
        reviewService.addReviewedRequestAction(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(request,
                actionPayload, RequestActionType.AIR_APPLICATION_REVIEWED, reviewer);
    }
}
