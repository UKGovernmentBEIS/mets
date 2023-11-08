package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

class EmpVariationCorsiaReviewMapperTest {

	private final EmpVariationCorsiaReviewMapper empVariationCorsiaReviewMapper = Mappers.getMapper(EmpVariationCorsiaReviewMapper.class);
	
	@Test
    void toEmpVariationCorsiaApplicationReviewRequestTaskPayload() {
        RequestTaskPayloadType empApplicationReviewRequestTaskPayloadType = 
        		RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD;
        ServiceContactDetails serviceContactDetails = 
        		ServiceContactDetails.builder().name("name").email("email").build();
        String reason = "details reason";
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = buildEmp();
        EmissionsMonitoringPlanCorsiaContainer originalEmpContainer = EmissionsMonitoringPlanCorsiaContainer
        		.builder()
        		.emissionsMonitoringPlan(buildEmp())
        		.serviceContactDetails(serviceContactDetails)
        		.build();

        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );

        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "abbreviations", List.of(true),
            "emissionSources", List.of(true)
        );

        EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .originalEmpContainer(originalEmpContainer)
            .empVariationDetails(EmpVariationCorsiaDetails.builder().reason(reason).build())
            .empVariationDetailsCompleted(true)
            .empSectionsCompleted(empSectionsCompleted)
            .empAttachments(empAttachments)
            .build();

        String operatorName = "anotherOperatorName";        
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .serviceContactDetails(serviceContactDetails)
            .build();

        EmpVariationCorsiaApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload =
        		empVariationCorsiaReviewMapper.toEmpVariationCorsiaApplicationReviewRequestTaskPayload(
                requestPayload, aviationAccountInfo, empApplicationReviewRequestTaskPayloadType);

        assertEquals(empApplicationReviewRequestTaskPayloadType,applicationReviewRequestTaskPayload.getPayloadType());
        assertEquals(aviationAccountInfo.getServiceContactDetails(), applicationReviewRequestTaskPayload.getServiceContactDetails());
        assertEquals(originalEmpContainer, applicationReviewRequestTaskPayload.getOriginalEmpContainer());

        EmissionsMonitoringPlanCorsia applicationReviewRequestTaskPayloadEmpObject = applicationReviewRequestTaskPayload.getEmissionsMonitoringPlan();

        assertEquals(emissionsMonitoringPlan.getAbbreviations(), applicationReviewRequestTaskPayloadEmpObject.getAbbreviations());
        assertEquals(emissionsMonitoringPlan.getEmissionSources(), applicationReviewRequestTaskPayloadEmpObject.getEmissionSources());

        EmpCorsiaOperatorDetails applicationReviewRequestTaskPayloadEmpObjectOperatorDetails = 
        		applicationReviewRequestTaskPayloadEmpObject.getOperatorDetails();

        assertNotNull(applicationReviewRequestTaskPayloadEmpObjectOperatorDetails);
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
    void toEmpVariationCorsiaApplicationApprovedRequestActionPayload() {
        RequestActionPayloadType approvedRequestActionPayloadType = 
        		RequestActionPayloadType.EMP_VARIATION_CORSIA_APPLICATION_APPROVED_PAYLOAD;
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();
        String reason = "details reason";
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = buildEmp();
        EmissionsMonitoringPlanCorsiaContainer originalEmpContainer = EmissionsMonitoringPlanCorsiaContainer
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
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = Map.of(
        		EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision.builder().type(EmpVariationReviewDecisionType.ACCEPTED).build(),
        		EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpVariationReviewDecision.builder().type(EmpVariationReviewDecisionType.ACCEPTED).build(),
        		EmpCorsiaReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES, EmpVariationReviewDecision.builder().type(EmpVariationReviewDecisionType.ACCEPTED).build()
        );
        
        EmpVariationReviewDecision empVariationDetailsReviewDecision = buildVariationDetailsReviewDecision();
        
        EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .originalEmpContainer(originalEmpContainer)
            .empAttachments(empAttachments)
            .reviewAttachments(reviewAttachments)
            .determination(determination)
            .empVariationDetails(EmpVariationCorsiaDetails.builder().reason(reason).build())
            .empVariationDetailsCompleted(true)
            .empVariationDetailsReviewDecision(empVariationDetailsReviewDecision)
            .decisionNotification(decisionNotification)
            .reviewGroupDecisions(reviewGroupDecisions)
            .build();

        String operatorName = "operatorName";
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .serviceContactDetails(serviceContactDetails)
            .build();
        
        Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "operatorUserId", RequestActionUserInfo.builder().name("operatorUserName").roleCode("admin").build(),
                "regulatorUserId", RequestActionUserInfo.builder().name("regulatorUserName").roleCode("admin").build()
            );

        EmpVariationCorsiaApplicationApprovedRequestActionPayload approvedRequestActionPayload =
        		empVariationCorsiaReviewMapper.toEmpVariationCorsiaApplicationApprovedRequestActionPayload
        		(requestPayload, aviationAccountInfo, usersInfo, approvedRequestActionPayloadType);

        assertThat(approvedRequestActionPayload.getPayloadType()).isEqualTo(approvedRequestActionPayloadType);
        assertEquals(originalEmpContainer, approvedRequestActionPayload.getOriginalEmpContainer());

        EmissionsMonitoringPlanCorsia taskActionPayloadEmpObject = approvedRequestActionPayload.getEmissionsMonitoringPlan();

        assertEquals(emissionsMonitoringPlan.getAbbreviations(), taskActionPayloadEmpObject.getAbbreviations());
        assertEquals(emissionsMonitoringPlan.getAdditionalDocuments(), taskActionPayloadEmpObject.getAdditionalDocuments());
        assertEquals(emissionsMonitoringPlan.getFlightAndAircraftProcedures(), taskActionPayloadEmpObject.getFlightAndAircraftProcedures());

        EmpCorsiaOperatorDetails taskActionPayloadEmpObjectOperatorDetails = taskActionPayloadEmpObject.getOperatorDetails();

        assertNotNull(taskActionPayloadEmpObjectOperatorDetails);
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getOperatorName(), taskActionPayloadEmpObjectOperatorDetails.getOperatorName());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getFlightIdentification(), taskActionPayloadEmpObjectOperatorDetails.getFlightIdentification());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getActivitiesDescription(), taskActionPayloadEmpObjectOperatorDetails.getActivitiesDescription());

        assertEquals(aviationAccountInfo.getServiceContactDetails(), approvedRequestActionPayload.getServiceContactDetails());
        assertEquals(determination, approvedRequestActionPayload.getDetermination());
        assertEquals(reason, approvedRequestActionPayload.getEmpVariationDetails().getReason());
        assertEquals(decisionNotification, approvedRequestActionPayload.getDecisionNotification());

        assertThat(approvedRequestActionPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(approvedRequestActionPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);
        assertThat(approvedRequestActionPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
        assertEquals(approvedRequestActionPayload.getEmpVariationDetailsReviewDecision(), empVariationDetailsReviewDecision);
    }

    @Test
    void toEmissionsMonitoringPlanUkEtsContainer() {
        EmissionsMonitoringPlanCorsia emp = buildEmp();

        ServiceContactDetails scd = ServiceContactDetails.builder()
            .email("email")
            .name("name")
            .roleCode("roleCode")
            .build();

        EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload = EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
            .emissionsMonitoringPlan(emp)
            .serviceContactDetails(scd)
            .empVariationDetails(EmpVariationCorsiaDetails.builder().reason("reason").build())
            .empVariationDetailsCompleted(Boolean.TRUE)
            .empVariationDetailsReviewDecision(buildVariationDetailsReviewDecision())
            .build();

        EmissionsMonitoringPlanCorsiaContainer result = empVariationCorsiaReviewMapper.toEmissionsMonitoringPlanCorsiaContainer(taskPayload);

        assertThat(result).isEqualTo(EmissionsMonitoringPlanCorsiaContainer.builder()
            .scheme(EmissionTradingScheme.CORSIA)
            .serviceContactDetails(scd)
            .emissionsMonitoringPlan(emp)
            .build());
    }
	
	private EmissionsMonitoringPlanCorsia buildEmp() {
		return EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
            .emissionSources(EmpEmissionSourcesCorsia.builder().build())
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .operatorName("empOperatorDetailsOperatorName")
                .flightIdentification(FlightIdentification.builder()
                    .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                    .icaoDesignators("icao designators")
                    .build())
                .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                    .operatorType(OperatorType.COMMERCIAL)
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
