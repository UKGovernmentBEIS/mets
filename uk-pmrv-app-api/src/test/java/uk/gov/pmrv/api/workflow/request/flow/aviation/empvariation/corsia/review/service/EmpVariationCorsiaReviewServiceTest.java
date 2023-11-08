package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation.EmpVariationCorsiaReviewDeterminationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaReviewServiceTest {

	@InjectMocks
    private EmpVariationCorsiaReviewService service;
	@Mock
    private EmpVariationCorsiaReviewDeterminationValidatorService reviewDeterminationValidatorService;
    @Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empCorsiaValidatorService;

    @Mock
    private RequestService requestService;
    
    @Test
    void saveEmpVariation() {
    	EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload taskActionPayload = 
    			EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload.builder()
    			.empVariationDetails(EmpVariationCorsiaDetails.builder()
    					.reason("test reason")
    					.build())
    			.empVariationDetailsCompleted(Boolean.TRUE)
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia
						.builder()
						.abbreviations(EmpAbbreviations.builder().exist(false).build())
						.operatorDetails(EmpCorsiaOperatorDetails.builder().build())
						.build())
    			.empSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("section2", true))
    			.build();
    	
    	EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload = 
    			EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia
    					.builder()
    					.operatorDetails(EmpCorsiaOperatorDetails.builder().build())
    					.build())
    			.build();
    	RequestTask requestTask = RequestTask.builder()
    			.payload(taskPayload)
    			.build();
    	
    	service.saveEmpVariation(taskActionPayload, requestTask);
    	
    	assertThat(taskPayload.getEmpVariationDetails()).isEqualTo(taskActionPayload.getEmpVariationDetails());
    	assertThat(taskPayload.getEmpVariationDetailsCompleted()).isEqualTo(taskActionPayload.getEmpVariationDetailsCompleted());
    	assertThat(taskPayload.getEmissionsMonitoringPlan()).isEqualTo(taskActionPayload.getEmissionsMonitoringPlan());
    	assertThat(taskPayload.getEmpSectionsCompleted()).isEqualTo(taskActionPayload.getEmpSectionsCompleted());
    	assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
    }
    
    @Test
    void saveReviewDecision() {
        EmpVariationCorsiaSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload = 
        	EmpVariationCorsiaSaveReviewGroupDecisionRequestTaskActionPayload.builder()
        	.decision(
                EmpVariationReviewDecision.builder()
                    .type(EmpVariationReviewDecisionType.ACCEPTED)
                    .details(EmpAcceptedVariationDecisionDetails.builder()
                    		.notes("test notes")
                    		.variationScheduleItems(List.of("change1","change2"))
                    		.build())
                    .build()
    				)
            .empSectionsCompleted(Map.of("section1", List.of(true, false)))
            .group(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
            .reviewSectionsCompleted(Map.of("section", true))
            .build();

        EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload = 
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload
        		.builder()
        		.determination(EmpVariationDetermination.builder().type(EmpVariationDeterminationType.APPROVED).build())
        		.build();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();

        service.saveReviewGroupDecision(taskActionPayload, requestTask);

        assertThat(taskPayload.getEmpVariationDetailsReviewDecision()).isNull();
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
        assertThat(taskPayload.getReviewGroupDecisions().get(taskActionPayload.getGroup())).isEqualTo(taskActionPayload.getDecision());
        assertThat(taskPayload.getEmpSectionsCompleted()).isEqualTo(taskActionPayload.getEmpSectionsCompleted());
        verify(reviewDeterminationValidatorService, times(1)).isValid(taskPayload, EmpVariationDeterminationType.APPROVED);
    }

    @Test
    void saveReviewGroupDetailsDecision() {
        EmpVariationCorsiaSaveDetailsReviewGroupDecisionRequestTaskActionPayload taskActionPayload = 
        	EmpVariationCorsiaSaveDetailsReviewGroupDecisionRequestTaskActionPayload.builder()
	        .decision(
            		EmpVariationReviewDecision.builder()
                    .type(EmpVariationReviewDecisionType.ACCEPTED)
                    .details(EmpAcceptedVariationDecisionDetails.builder()
                    		.notes("test notes")
                    		.variationScheduleItems(List.of("change1","change2"))
                    		.build())
                    .build()
            )
            .empVariationDetailsCompleted(true)
            .reviewSectionsCompleted(Map.of("section", true))
            .build();

        EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload = EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();

        service.saveDetailsReviewGroupDecision(taskActionPayload, requestTask);

        assertThat(taskPayload.getEmpVariationDetailsReviewDecision()).isEqualTo(taskActionPayload.getDecision());
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
        assertEquals(Boolean.TRUE, taskPayload.getEmpVariationDetailsCompleted());
    }
    
    @Test
    void saveDetermination() {
        EmpVariationDetermination determination = EmpVariationDetermination.builder()
            .type(EmpVariationDeterminationType.APPROVED)
            .reason("reason")
            .build();

        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "abbreviations", true,
            "additionalDocuments", true
        );

        EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
        		EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload.builder()
                .determination(determination)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build();

        EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload =
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder().build())
                .payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD)
                .build();

        RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        service.saveDetermination(taskActionPayload, requestTask);

        EmpVariationCorsiaApplicationReviewRequestTaskPayload updatedRequestTaskPayload = 
        		(EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertEquals(determination, updatedRequestTaskPayload.getDetermination());
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(taskActionPayload.getReviewSectionsCompleted());
    }
    
    @Test
    void saveDecisionNotification() {
        String reviewer = "regUser";
        PmrvUser pmrvUser = PmrvUser.builder().userId(reviewer).build();
        EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD)
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operUser")).signatory(reviewer).build();

        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .operatorDetails(EmpCorsiaOperatorDetails.builder().operatorName("name").build())
            .build();
        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );
        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "operatorDetails", List.of(true)
        );
        Map<UUID, String> reviewAttachments = Map.of(
            UUID.randomUUID(), "reviewAttachment"
        );
        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "operatorDetails", true
            );
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = Map.of(
        		EmpCorsiaReviewGroup.OPERATOR_DETAILS,
            EmpVariationReviewDecision.builder().type(EmpVariationReviewDecisionType.ACCEPTED)
            	.details(ReviewDecisionDetails.builder().notes("notes").build()).build()
        );
        EmpVariationCorsiaDetails details = EmpVariationCorsiaDetails.builder().reason("test reason").build();
        EmpVariationReviewDecision detailsReviewDecision = EmpVariationReviewDecision.builder().build();
        EmpVariationDetermination determination = EmpVariationDetermination.builder().type(EmpVariationDeterminationType.APPROVED).build();
        EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empVariationDetails(details)
			.empVariationDetailsCompleted(Boolean.TRUE)
			.empVariationDetailsReviewDecision(detailsReviewDecision)
			.empVariationDetailsReviewCompleted(Boolean.TRUE)
            .empAttachments(empAttachments)
            .empSectionsCompleted(empSectionsCompleted)
            .reviewAttachments(reviewAttachments)
            .reviewSectionsCompleted(reviewSectionsCompleted)
            .reviewGroupDecisions(reviewGroupDecisions)
            .determination(determination)
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .request(request)
            .build();

        //invoke
        service.saveDecisionNotification(requestTask, decisionNotification, pmrvUser);

        EmpVariationCorsiaRequestPayload updatedRequestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());

        assertEquals(emissionsMonitoringPlan, updatedRequestPayload.getEmissionsMonitoringPlan());
        assertEquals(determination, updatedRequestPayload.getDetermination());
        assertEquals(decisionNotification, updatedRequestPayload.getDecisionNotification());
        assertEquals(reviewer, updatedRequestPayload.getRegulatorReviewer());
        assertEquals(details, updatedRequestPayload.getEmpVariationDetails());
        assertEquals(Boolean.TRUE, updatedRequestPayload.getEmpVariationDetailsCompleted());
        assertEquals(detailsReviewDecision, updatedRequestPayload.getEmpVariationDetailsReviewDecision());
        assertEquals(Boolean.TRUE, updatedRequestPayload.getEmpVariationDetailsReviewCompleted());
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(updatedRequestPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
    }

    @Test
    void saveRequestPeerReviewAction() {
        String selectedPeerReviewer = "peerReviewer";
        String reviewer = "reviewer";
        String reason = "reason";
        PmrvUser pmrvUser = PmrvUser.builder().userId(reviewer).build();
        EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD)
            .build();

        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .operatorDetails(EmpCorsiaOperatorDetails.builder().operatorName("name").build())
            .build();
        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );
        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "operatorDetails", List.of(true)
        );
        Map<UUID, String> reviewAttachments = Map.of(
            UUID.randomUUID(), "reviewAttachment"
        );
        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "operatorDetails", true
        );
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = Map.of(
        		EmpCorsiaReviewGroup.OPERATOR_DETAILS,
            EmpVariationReviewDecision.builder().type(EmpVariationReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails
            		.builder()
            		.notes("notes")
            		.build())
            .build());
        EmpVariationDetermination determination = EmpVariationDetermination.builder()
            .type(EmpVariationDeterminationType.DEEMED_WITHDRAWN)
            .reason(reason)
            .build();
        EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empAttachments(empAttachments)
            .empVariationDetails(EmpVariationCorsiaDetails.builder().reason(reason).build())
            .empVariationDetailsReviewCompleted(true)
            .empVariationDetailsCompleted(true)
            .empSectionsCompleted(empSectionsCompleted)
            .reviewAttachments(reviewAttachments)
            .reviewSectionsCompleted(reviewSectionsCompleted)
            .reviewGroupDecisions(reviewGroupDecisions)
            .determination(determination)
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .request(request)
            .build();

        service.saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, pmrvUser);

        EmpVariationCorsiaRequestPayload updatedRequestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());

        assertEquals(emissionsMonitoringPlan, updatedRequestPayload.getEmissionsMonitoringPlan());
        assertEquals(determination, updatedRequestPayload.getDetermination());
        assertEquals(selectedPeerReviewer, updatedRequestPayload.getRegulatorPeerReviewer());
        assertEquals(reviewer, updatedRequestPayload.getRegulatorReviewer());
        assertEquals(reason, updatedRequestPayload.getEmpVariationDetails().getReason());
        assertTrue(updatedRequestPayload.getEmpVariationDetailsCompleted());
        assertTrue(updatedRequestPayload.getEmpVariationDetailsReviewCompleted());
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(updatedRequestPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
    }
}
