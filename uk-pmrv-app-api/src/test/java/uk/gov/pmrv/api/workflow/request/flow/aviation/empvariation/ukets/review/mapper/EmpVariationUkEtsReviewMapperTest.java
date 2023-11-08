package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.applicationtimeframe.EmpApplicationTimeframeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsreductionclaim.EmpEmissionsReductionClaim;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

class EmpVariationUkEtsReviewMapperTest {

	private final EmpVariationUkEtsReviewMapper empVariationUkEtsReviewMapper = Mappers.getMapper(EmpVariationUkEtsReviewMapper.class);
	
	@Test
    void toEmpVariationUkEtsApplicationReviewRequestTaskPayload() {
        RequestTaskPayloadType empApplicationReviewRequestTaskPayloadType = RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_REVIEW_PAYLOAD;
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();
        String reason = "details reason";
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = buildEmp();
        EmissionsMonitoringPlanUkEtsContainer originalEmpContainer = EmissionsMonitoringPlanUkEtsContainer
        		.builder()
        		.emissionsMonitoringPlan(buildEmp())
        		.serviceContactDetails(serviceContactDetails)
        		.build();

        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );

        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "abbreviations", List.of(true),
            "applicationTimeframeInfo", List.of(true),
            "emissionsMonitoringApproach", List.of(true)
        );

        EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .originalEmpContainer(originalEmpContainer)
            .empVariationDetails(EmpVariationUkEtsDetails.builder().reason(reason).build())
            .empVariationDetailsCompleted(true)
            .empSectionsCompleted(empSectionsCompleted)
            .empAttachments(empAttachments)
            .build();

        String operatorName = "anotherOperatorName";
        String crcoCode = "anotherCode";
        
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .crcoCode(crcoCode)
            .serviceContactDetails(serviceContactDetails)
            .build();

        EmpVariationUkEtsApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload =
        		empVariationUkEtsReviewMapper.toEmpVariationUkEtsApplicationReviewRequestTaskPayload(
                requestPayload, aviationAccountInfo, empApplicationReviewRequestTaskPayloadType);

        assertEquals(empApplicationReviewRequestTaskPayloadType,applicationReviewRequestTaskPayload.getPayloadType());
        assertEquals(aviationAccountInfo.getServiceContactDetails(), applicationReviewRequestTaskPayload.getServiceContactDetails());
        assertEquals(originalEmpContainer, applicationReviewRequestTaskPayload.getOriginalEmpContainer());

        EmissionsMonitoringPlanUkEts applicationReviewRequestTaskPayloadEmpObject = applicationReviewRequestTaskPayload.getEmissionsMonitoringPlan();

        assertEquals(emissionsMonitoringPlan.getAbbreviations(), applicationReviewRequestTaskPayloadEmpObject.getAbbreviations());
        assertEquals(emissionsMonitoringPlan.getApplicationTimeframeInfo(), applicationReviewRequestTaskPayloadEmpObject.getApplicationTimeframeInfo());
        assertEquals(emissionsMonitoringPlan.getEmissionsMonitoringApproach(), applicationReviewRequestTaskPayloadEmpObject.getEmissionsMonitoringApproach());

        EmpOperatorDetails applicationReviewRequestTaskPayloadEmpObjectOperatorDetails = applicationReviewRequestTaskPayloadEmpObject.getOperatorDetails();

        assertNotNull(applicationReviewRequestTaskPayloadEmpObjectOperatorDetails);
        assertEquals(aviationAccountInfo.getCrcoCode(), applicationReviewRequestTaskPayloadEmpObjectOperatorDetails.getCrcoCode());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getOperatorName(), applicationReviewRequestTaskPayloadEmpObjectOperatorDetails.getOperatorName());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getFlightIdentification(), applicationReviewRequestTaskPayloadEmpObjectOperatorDetails.getFlightIdentification());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getActivitiesDescription(), applicationReviewRequestTaskPayloadEmpObjectOperatorDetails.getActivitiesDescription());

        assertThat(applicationReviewRequestTaskPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(applicationReviewRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertEquals(reason, applicationReviewRequestTaskPayload.getEmpVariationDetails().getReason());

        assertThat(applicationReviewRequestTaskPayload.getReviewSectionsCompleted()).isEmpty();
        assertThat(applicationReviewRequestTaskPayload.getReviewGroupDecisions()).isEmpty();
        assertThat(applicationReviewRequestTaskPayload.getReviewAttachments()).isEmpty();
        assertThat(applicationReviewRequestTaskPayload.getEmpVariationDetailsReviewDecision()).isNull();
    }
	
	@Test
    void toEmpVariationUkEtsApplicationApprovedRequestActionPayload() {
        RequestActionPayloadType approvedRequestActionPayloadType = RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_APPROVED_PAYLOAD;
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();
        String reason = "details reason";
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = buildEmp();
        EmissionsMonitoringPlanUkEtsContainer originalEmpContainer = EmissionsMonitoringPlanUkEtsContainer
        		.builder()
        		.emissionsMonitoringPlan(buildEmp())
        		.serviceContactDetails(serviceContactDetails)
        		.build();

        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );

        Map<UUID, String> reviewAttachments = Map.of(
            UUID.randomUUID(), "reviewAttachment"
        );

        EmpVariationDetermination determination = EmpVariationDetermination.builder()
            .type(EmpVariationDeterminationType.APPROVED)
            .reason("determination reason")
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operatorUserId"))
            .signatory("regulatorUserId")
            .build();
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = Map.of(
            EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision.builder().type(EmpVariationReviewDecisionType.ACCEPTED).build(),
            EmpUkEtsReviewGroup.LATE_SUBMISSION, EmpVariationReviewDecision.builder().type(EmpVariationReviewDecisionType.ACCEPTED).build(),
            EmpUkEtsReviewGroup.MONITORING_APPROACH, EmpVariationReviewDecision.builder().type(EmpVariationReviewDecisionType.ACCEPTED).build()
        );
        
        EmpVariationReviewDecision empVariationDetailsReviewDecision = buildVariationDetailsReviewDecision();
        
        EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .originalEmpContainer(originalEmpContainer)
            .empAttachments(empAttachments)
            .reviewAttachments(reviewAttachments)
            .determination(determination)
            .empVariationDetails(EmpVariationUkEtsDetails.builder().reason(reason).build())
            .empVariationDetailsCompleted(true)
            .empVariationDetailsReviewDecision(empVariationDetailsReviewDecision)
            .decisionNotification(decisionNotification)
            .reviewGroupDecisions(reviewGroupDecisions)
            .build();

        String operatorName = "operatorName";
        String crcoCode = "crcoCode";
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .crcoCode(crcoCode)
            .serviceContactDetails(serviceContactDetails)
            .build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of(
            "operatorUserId", RequestActionUserInfo.builder().name("operatorUserName").roleCode("admin").build(),
            "regulatorUserId", RequestActionUserInfo.builder().name("regulatorUserName").roleCode("admin").build()
        );

        EmpVariationUkEtsApplicationApprovedRequestActionPayload approvedRequestActionPayload =
        		empVariationUkEtsReviewMapper.toEmpVariationUkEtsApplicationApprovedRequestActionPayload(requestPayload, aviationAccountInfo,
                usersInfo, approvedRequestActionPayloadType);

        assertThat(approvedRequestActionPayload.getPayloadType()).isEqualTo(approvedRequestActionPayloadType);
        assertEquals(originalEmpContainer, approvedRequestActionPayload.getOriginalEmpContainer());

        EmissionsMonitoringPlanUkEts taskActionPayloadEmpObject = approvedRequestActionPayload.getEmissionsMonitoringPlan();

        assertEquals(emissionsMonitoringPlan.getAbbreviations(), taskActionPayloadEmpObject.getAbbreviations());
        assertEquals(emissionsMonitoringPlan.getApplicationTimeframeInfo(), taskActionPayloadEmpObject.getApplicationTimeframeInfo());
        assertEquals(emissionsMonitoringPlan.getEmissionsMonitoringApproach(), taskActionPayloadEmpObject.getEmissionsMonitoringApproach());

        EmpOperatorDetails taskActionPayloadEmpObjectOperatorDetails = taskActionPayloadEmpObject.getOperatorDetails();

        assertNotNull(taskActionPayloadEmpObjectOperatorDetails);
        assertEquals(aviationAccountInfo.getCrcoCode(), taskActionPayloadEmpObjectOperatorDetails.getCrcoCode());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getOperatorName(), taskActionPayloadEmpObjectOperatorDetails.getOperatorName());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getFlightIdentification(), taskActionPayloadEmpObjectOperatorDetails.getFlightIdentification());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getActivitiesDescription(), taskActionPayloadEmpObjectOperatorDetails.getActivitiesDescription());

        assertEquals(aviationAccountInfo.getServiceContactDetails(), approvedRequestActionPayload.getServiceContactDetails());
        assertEquals(determination, approvedRequestActionPayload.getDetermination());
        assertEquals(decisionNotification, approvedRequestActionPayload.getDecisionNotification());
        assertEquals(reason, approvedRequestActionPayload.getEmpVariationDetails().getReason());

        assertThat(approvedRequestActionPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(approvedRequestActionPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);
        assertThat(approvedRequestActionPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
        assertEquals(approvedRequestActionPayload.getEmpVariationDetailsReviewDecision(), empVariationDetailsReviewDecision);
    }

    @Test
    void toEmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload() {
        EmpVariationDetermination determination = EmpVariationDetermination.builder()
            .type(EmpVariationDeterminationType.DEEMED_WITHDRAWN)
            .reason("determination reason")
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operatorUserId"))
            .signatory("regulatorUserId")
            .build();

        EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
            .determination(determination)
            .decisionNotification(decisionNotification)
            .build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of(
            "operatorUserId", RequestActionUserInfo.builder().name("operatorUserName").roleCode("admin").build(),
            "regulatorUserId", RequestActionUserInfo.builder().name("regulatorUserName").roleCode("admin").build()
        );

        EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload deemedWithdrawnRequestActionPayload =
        		empVariationUkEtsReviewMapper.toEmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload(requestPayload, usersInfo,
                RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD);

        assertEquals(RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD,
            deemedWithdrawnRequestActionPayload.getPayloadType());
        assertEquals(determination, deemedWithdrawnRequestActionPayload.getDetermination());
        assertEquals(decisionNotification, deemedWithdrawnRequestActionPayload.getDecisionNotification());
        assertThat(deemedWithdrawnRequestActionPayload.getUsersInfo()).containsExactlyInAnyOrderEntriesOf(usersInfo);
    }
    
    @Test
    void toEmpVariationUkEtsApplicationRejectedRequestActionPayload() {
        EmpVariationDetermination determination = EmpVariationDetermination.builder()
            .type(EmpVariationDeterminationType.REJECTED)
            .reason("determination reason")
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operatorUserId"))
            .signatory("regulatorUserId")
            .build();

        EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
            .determination(determination)
            .decisionNotification(decisionNotification)
            .build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of(
            "operatorUserId", RequestActionUserInfo.builder().name("operatorUserName").roleCode("admin").build(),
            "regulatorUserId", RequestActionUserInfo.builder().name("regulatorUserName").roleCode("admin").build()
        );

        EmpVariationUkEtsApplicationRejectedRequestActionPayload rejectedRequestActionPayload =
        		empVariationUkEtsReviewMapper.toEmpVariationUkEtsApplicationRejectedRequestActionPayload(requestPayload, usersInfo,
                RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_REJECTED_PAYLOAD);

        assertEquals(RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_REJECTED_PAYLOAD,
        		rejectedRequestActionPayload.getPayloadType());
        assertEquals(determination, rejectedRequestActionPayload.getDetermination());
        assertEquals(decisionNotification, rejectedRequestActionPayload.getDecisionNotification());
        assertThat(rejectedRequestActionPayload.getUsersInfo()).containsExactlyInAnyOrderEntriesOf(usersInfo);
    }
    
    @Test
    void toEmissionsMonitoringPlanUkEtsContainer() {
    	EmissionsMonitoringPlanUkEts emp = buildEmp();
    	
    	ServiceContactDetails scd = ServiceContactDetails.builder()
    			.email("email")
    			.name("name")
    			.roleCode("roleCode")
    			.build();
    	
    	EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload = EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
    			.emissionsMonitoringPlan(emp)
    			.serviceContactDetails(scd)
    			.empVariationDetails(EmpVariationUkEtsDetails.builder().reason("reason").build())
    			.empVariationDetailsCompleted(Boolean.TRUE)
    			.empVariationDetailsReviewDecision(buildVariationDetailsReviewDecision())
    			.build();
    	   	
    	EmissionsMonitoringPlanUkEtsContainer result = empVariationUkEtsReviewMapper.toEmissionsMonitoringPlanUkEtsContainer(taskPayload);
    	
    	assertThat(result).isEqualTo(EmissionsMonitoringPlanUkEtsContainer.builder()
    			.scheme(EmissionTradingScheme.UK_ETS_AVIATION)
    			.serviceContactDetails(scd)
    			.emissionsMonitoringPlan(emp)
    		.build());
    }
	
	private EmissionsMonitoringPlanUkEts buildEmp() {
		return EmissionsMonitoringPlanUkEts.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .applicationTimeframeInfo(EmpApplicationTimeframeInfo.builder().dateOfStart(LocalDate.now()).submittedOnTime(Boolean.TRUE).build())
            .emissionsMonitoringApproach(FuelMonitoringApproach.builder().build())
            .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
            .emissionsReductionClaim(EmpEmissionsReductionClaim.builder().exist(false).build())
            .operatorDetails(EmpOperatorDetails.builder()
                .operatorName("empOperatorDetailsOperatorName")
                .crcoCode("empOperatorDetailsCrcoCode")
                .flightIdentification(FlightIdentification.builder()
                    .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                    .icaoDesignators("icao designators")
                    .build())
                .activitiesDescription(ActivitiesDescription.builder()
                    .operatorType(OperatorType.COMMERCIAL)
                    .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                    .flightTypes(Set.of(FlightType.SCHEDULED))
                    .activityDescription("activity description")
                    .build())
                .build())
            .build();
	}
	
	private EmpVariationReviewDecision buildVariationDetailsReviewDecision() {
		return EmpVariationReviewDecision.builder()
		        .type(EmpVariationReviewDecisionType.ACCEPTED)
		        .details(EmpAcceptedVariationDecisionDetails.builder()
		        		.notes("test notes")
		        		.variationScheduleItems(List.of("change1","change2"))
		        		.build())
		        .build();
	}
	
	
}
