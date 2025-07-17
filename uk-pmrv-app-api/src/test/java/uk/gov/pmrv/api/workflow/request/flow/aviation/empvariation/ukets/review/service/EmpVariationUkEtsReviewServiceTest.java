package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service;

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

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation.EmpVariationUkEtsReviewDeterminationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsReviewServiceTest {
	
	@InjectMocks
    private EmpVariationUkEtsReviewService service;
	
	@Mock
    private EmpVariationUkEtsReviewDeterminationValidatorService reviewDeterminationValidatorService;
	
	@Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empUkEtsValidatorService;
	
	@Mock
    private RequestService requestService;
    
    @Test
    void saveEmpVariation() {
    	EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload taskActionPayload = EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload.builder()
    			.empVariationDetails(EmpVariationUkEtsDetails.builder()
    					.reason("test reason")
    					.build())
    			.empVariationDetailsCompleted(Boolean.TRUE)
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts
						.builder()
						.abbreviations(EmpAbbreviations.builder().exist(false).build())
						.operatorDetails(EmpOperatorDetails.builder().build())
						.build())
    			.empSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("section2", true))
    			.build();
    	
    	EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload = EmpVariationUkEtsApplicationReviewRequestTaskPayload
    			.builder()
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts
    					.builder()
    					.operatorDetails(EmpOperatorDetails.builder().build())
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
        EmpVariationUkEtsSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload = EmpVariationUkEtsSaveReviewGroupDecisionRequestTaskActionPayload.builder()
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
            .group(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
            .reviewSectionsCompleted(Map.of("section", true))
            .build();

        EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload = EmpVariationUkEtsApplicationReviewRequestTaskPayload
        		.builder()
        		.determination(EmpVariationDetermination.builder().type(EmpVariationDeterminationType.APPROVED).build())
        		.build();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();

        service.saveReviewGroupDecision(taskActionPayload, requestTask);

        assertThat(taskPayload.getEmpVariationDetailsReviewDecision()).isNull();
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
        assertThat(taskPayload.getReviewGroupDecisions().containsValue(taskActionPayload.getDecision()));
        assertThat(taskPayload.getEmpSectionsCompleted()).isEqualTo(taskActionPayload.getEmpSectionsCompleted());
        verify(reviewDeterminationValidatorService, times(1)).isValid(taskPayload, EmpVariationDeterminationType.APPROVED);
    }

    @Test
    void saveReviewGroupDetailsDecision() {
        EmpVariationUkEtsSaveDetailsReviewGroupDecisionRequestTaskActionPayload taskActionPayload = EmpVariationUkEtsSaveDetailsReviewGroupDecisionRequestTaskActionPayload.builder()
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

        EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload = EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder().build();
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

        EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
        		EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload.builder()
                .determination(determination)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build();

        EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload =
        		EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .payloadType(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_REVIEW_PAYLOAD)
                .build();

        RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        service.saveDetermination(taskActionPayload, requestTask);

        EmpVariationUkEtsApplicationReviewRequestTaskPayload
            updatedRequestTaskPayload = (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertEquals(determination, updatedRequestTaskPayload.getDetermination());
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(taskActionPayload.getReviewSectionsCompleted());
    }
    
    @Test
    void saveDecisionNotification() {
        String reviewer = "regUser";
        AppUser appUser = AppUser.builder().userId(reviewer).build();
        EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operUser")).signatory(reviewer).build();

        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .operatorDetails(EmpOperatorDetails.builder().operatorName("name").crcoCode("crcoCode").build())
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
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = Map.of(
            EmpUkEtsReviewGroup.OPERATOR_DETAILS,
            EmpVariationReviewDecision.builder().type(EmpVariationReviewDecisionType.ACCEPTED)
            	.details(ReviewDecisionDetails.builder().notes("notes").build()).build()
        );
        EmpVariationUkEtsDetails details = EmpVariationUkEtsDetails.builder().reason("test reason").build();
        EmpVariationReviewDecision detailsReviewDecision = EmpVariationReviewDecision.builder().build();
        EmpVariationDetermination determination = EmpVariationDetermination.builder().type(EmpVariationDeterminationType.APPROVED).build();
        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_REVIEW_PAYLOAD)
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
        service.saveDecisionNotification(requestTask, decisionNotification, appUser);

        EmpVariationUkEtsRequestPayload updatedRequestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());

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
        AppUser appUser = AppUser.builder().userId(reviewer).build();
        EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
            .build();

        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .operatorDetails(EmpOperatorDetails.builder().operatorName("name").crcoCode("crcoCode").build())
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
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = Map.of(
            EmpUkEtsReviewGroup.OPERATOR_DETAILS,
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
        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_REVIEW_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empAttachments(empAttachments)
            .empVariationDetails(EmpVariationUkEtsDetails.builder().reason(reason).build())
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

        service.saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, appUser);

        EmpVariationUkEtsRequestPayload updatedRequestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());

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
    
    @Test
    void saveRequestReturnForAmends() {
        String reviewer = "reviewer";
        AppUser appUser = AppUser.builder().userId(reviewer).build();
        EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
            .build();

        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
                .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .build();
        
        EmpVariationUkEtsDetails details = EmpVariationUkEtsDetails.builder().reason("test reason").build();
        EmpVariationReviewDecision detailsReviewDecision = EmpVariationReviewDecision.builder().build();
        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );
        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "abbreviations", List.of(true)
        );
        Map<UUID, String> reviewAttachments = Map.of(
            UUID.randomUUID(), "reviewAttachment"
        );
        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "abbreviations", true
        );
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = Map.of(
            EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
            EmpVariationReviewDecision.builder()
            .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ReviewDecisionDetails
            		.builder()
            		.notes("notes")
            		.build())
            .build()
        );
        
        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empVariationDetails(details)
			.empVariationDetailsCompleted(Boolean.TRUE)
            .empAttachments(empAttachments)
            .empSectionsCompleted(empSectionsCompleted)
            .reviewAttachments(reviewAttachments)
            .reviewSectionsCompleted(reviewSectionsCompleted)
            .reviewGroupDecisions(reviewGroupDecisions)
            .empVariationDetailsReviewDecision(detailsReviewDecision)
            .empVariationDetailsReviewCompleted(Boolean.TRUE)
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .request(request)
            .build();

        service.saveRequestReturnForAmends(requestTask, appUser);

        EmpVariationUkEtsRequestPayload updatedRequestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());

        assertEquals(emissionsMonitoringPlan, updatedRequestPayload.getEmissionsMonitoringPlan());
        assertEquals(reviewer, updatedRequestPayload.getRegulatorReviewer());
        assertEquals(details, updatedRequestPayload.getEmpVariationDetails());
        assertEquals(detailsReviewDecision, updatedRequestPayload.getEmpVariationDetailsReviewDecision());
        assertTrue(updatedRequestPayload.getEmpVariationDetailsCompleted());
        assertTrue(updatedRequestPayload.getEmpVariationDetailsReviewCompleted());
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(updatedRequestPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
    }
}
