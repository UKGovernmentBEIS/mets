package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.mapper;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.FuelMonitoringApproachCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

class EmpCorsiaReviewMapperTest {

    private final EmpCorsiaReviewMapper empCorsiaReviewMapper = Mappers.getMapper(EmpCorsiaReviewMapper.class);

    @Test
    void toEmpIssuanceCorsiaApplicationReviewRequestTaskPayload() {
        RequestTaskPayloadType empApplicationReviewRequestTaskPayloadType = RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD;
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .emissionsMonitoringApproach(FuelMonitoringApproachCorsia.builder().build())
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .operatorName("operatorName")
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

        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );

        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "abbreviations", List.of(true),
            "operatorDetails", List.of(true),
            "monitoringApproach", List.of(true),
            "managementProcedures", List.of(true)
        );

        EmpIssuanceCorsiaRequestPayload empIssuanceCorsiaRequestPayload = EmpIssuanceCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empSectionsCompleted(empSectionsCompleted)
            .empAttachments(empAttachments)
            .build();

        String operatorName = "anotherOperatorName";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .serviceContactDetails(serviceContactDetails)
            .build();

        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload =
            empCorsiaReviewMapper.toEmpIssuanceCorsiaApplicationReviewRequestTaskPayload(
                empIssuanceCorsiaRequestPayload, aviationAccountInfo, empApplicationReviewRequestTaskPayloadType);

        assertEquals(empApplicationReviewRequestTaskPayloadType,applicationReviewRequestTaskPayload.getPayloadType());
        assertEquals(aviationAccountInfo.getServiceContactDetails(), applicationReviewRequestTaskPayload.getServiceContactDetails());

        EmissionsMonitoringPlanCorsia applicationReviewRequestTaskPayloadEmpObject = applicationReviewRequestTaskPayload.getEmissionsMonitoringPlan();

        assertEquals(emissionsMonitoringPlan.getAbbreviations(), applicationReviewRequestTaskPayloadEmpObject.getAbbreviations());
        assertEquals(emissionsMonitoringPlan.getEmissionsMonitoringApproach(), applicationReviewRequestTaskPayloadEmpObject.getEmissionsMonitoringApproach());

        EmpCorsiaOperatorDetails applicationReviewRequestTaskPayloadEmpObjectOperatorDetails = applicationReviewRequestTaskPayloadEmpObject.getOperatorDetails();

        assertNotNull(applicationReviewRequestTaskPayloadEmpObjectOperatorDetails);
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getOperatorName(), applicationReviewRequestTaskPayloadEmpObjectOperatorDetails.getOperatorName());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getFlightIdentification(), applicationReviewRequestTaskPayloadEmpObjectOperatorDetails.getFlightIdentification());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getActivitiesDescription(), applicationReviewRequestTaskPayloadEmpObjectOperatorDetails.getActivitiesDescription());

        assertThat(applicationReviewRequestTaskPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(applicationReviewRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);

        assertThat(applicationReviewRequestTaskPayload.getReviewSectionsCompleted()).isEmpty();
        assertThat(applicationReviewRequestTaskPayload.getReviewGroupDecisions()).isEmpty();
        assertThat(applicationReviewRequestTaskPayload.getReviewAttachments()).isEmpty();
    }

    @Test
    void toEmpIssuanceCorsiaApplicationApprovedRequestActionPayload() {
        RequestActionPayloadType empApplicationApprovedRequestActionPayloadType = RequestActionPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED_PAYLOAD;
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .emissionsMonitoringApproach(
                FuelMonitoringApproachCorsia.builder().build())
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .operatorName("empOpertorDetailsOperatorName")
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

        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );

        Map<UUID, String> reviewAttachments = Map.of(
            UUID.randomUUID(), "reviewAttachment"
        );

        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.APPROVED)
            .reason("determination reason")
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operatorUserId"))
            .signatory("regulatorUserId")
            .build();
        Map<EmpCorsiaReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = Map.of(
            EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpIssuanceReviewDecision.builder().type(
                EmpReviewDecisionType.ACCEPTED).build(),
            EmpCorsiaReviewGroup.DATA_GAPS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build(),
            EmpCorsiaReviewGroup.MONITORING_APPROACH, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
        );

        EmpIssuanceCorsiaRequestPayload empIssuanceCorsiaRequestPayload = EmpIssuanceCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empAttachments(empAttachments)
            .reviewAttachments(reviewAttachments)
            .determination(determination)
            .decisionNotification(decisionNotification)
            .reviewGroupDecisions(reviewGroupDecisions)
            .build();

        String operatorName = "operatorName";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .serviceContactDetails(serviceContactDetails)
            .build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of(
            "operatorUserId", RequestActionUserInfo.builder().name("operatorUserName").roleCode("admin").build(),
            "regulatorUserId", RequestActionUserInfo.builder().name("regulatorUserName").roleCode("admin").build()
        );

        EmpIssuanceCorsiaApplicationApprovedRequestActionPayload applicationApprovedRequestActionPayload =
            empCorsiaReviewMapper.toEmpIssuanceCorsiaApplicationApprovedRequestActionPayload(empIssuanceCorsiaRequestPayload, aviationAccountInfo,
                usersInfo, empApplicationApprovedRequestActionPayloadType);

        assertThat(applicationApprovedRequestActionPayload.getPayloadType()).isEqualTo(empApplicationApprovedRequestActionPayloadType);

        EmissionsMonitoringPlanCorsia taskActionPayloadEmpObject = applicationApprovedRequestActionPayload.getEmissionsMonitoringPlan();

        assertEquals(emissionsMonitoringPlan.getAbbreviations(), taskActionPayloadEmpObject.getAbbreviations());
        assertEquals(emissionsMonitoringPlan.getEmissionsMonitoringApproach(), taskActionPayloadEmpObject.getEmissionsMonitoringApproach());

        EmpCorsiaOperatorDetails taskActionPayloadEmpObjectOperatorDetails = taskActionPayloadEmpObject.getOperatorDetails();

        assertNotNull(taskActionPayloadEmpObjectOperatorDetails);
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getOperatorName(), taskActionPayloadEmpObjectOperatorDetails.getOperatorName());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getFlightIdentification(), taskActionPayloadEmpObjectOperatorDetails.getFlightIdentification());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getActivitiesDescription(), taskActionPayloadEmpObjectOperatorDetails.getActivitiesDescription());

        assertEquals(aviationAccountInfo.getServiceContactDetails(), applicationApprovedRequestActionPayload.getServiceContactDetails());
        assertEquals(determination, applicationApprovedRequestActionPayload.getDetermination());
        assertEquals(decisionNotification, applicationApprovedRequestActionPayload.getDecisionNotification());

        assertThat(applicationApprovedRequestActionPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(applicationApprovedRequestActionPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);
        assertThat(applicationApprovedRequestActionPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
    }

    @Test
    void toEmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload() {
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.APPROVED)
            .reason("determination reason")
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operatorUserId"))
            .signatory("regulatorUserId")
            .build();

        EmpIssuanceCorsiaRequestPayload empIssuanceCorsiaRequestPayload = EmpIssuanceCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
            .determination(determination)
            .decisionNotification(decisionNotification)
            .build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of(
            "operatorUserId", RequestActionUserInfo.builder().name("operatorUserName").roleCode("admin").build(),
            "regulatorUserId", RequestActionUserInfo.builder().name("regulatorUserName").roleCode("admin").build()
        );

        EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload applicationDeemedWithdrawnRequestActionPayload =
            empCorsiaReviewMapper.toEmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload(empIssuanceCorsiaRequestPayload, usersInfo,
                RequestActionPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD);

        assertEquals(RequestActionPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD,
            applicationDeemedWithdrawnRequestActionPayload.getPayloadType());
        assertEquals(determination, applicationDeemedWithdrawnRequestActionPayload.getDetermination());
        assertEquals(decisionNotification, applicationDeemedWithdrawnRequestActionPayload.getDecisionNotification());
        assertThat(applicationDeemedWithdrawnRequestActionPayload.getUsersInfo()).containsExactlyInAnyOrderEntriesOf(usersInfo);
    }

    @Test
    void toEmissionsMonitoringPlanCorsiaContainer() {
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .emissionsMonitoringApproach(FuelMonitoringApproachCorsia.builder().build())
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

        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();

        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment1",
            UUID.randomUUID(), "attachment2"
        );

        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .serviceContactDetails(serviceContactDetails)
                .empAttachments(empAttachments)
                .build();

        EmissionsMonitoringPlanCorsiaContainer empCorsiaContainer =
            empCorsiaReviewMapper.toEmissionsMonitoringPlanCorsiaContainer(reviewRequestTaskPayload);

        assertEquals(EmissionTradingScheme.CORSIA, empCorsiaContainer.getScheme());
        assertEquals(emissionsMonitoringPlan, empCorsiaContainer.getEmissionsMonitoringPlan());
        assertThat(empCorsiaContainer.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
    }
}