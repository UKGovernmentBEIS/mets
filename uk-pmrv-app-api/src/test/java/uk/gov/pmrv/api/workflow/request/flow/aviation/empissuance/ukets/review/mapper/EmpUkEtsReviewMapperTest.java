package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.applicationtimeframe.EmpApplicationTimeframeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmpUkEtsReviewMapperTest {

    private final EmpUkEtsReviewMapper empUkEtsReviewMapper = Mappers.getMapper(EmpUkEtsReviewMapper.class);

    @Test
    void toEmpIssuanceUkEtsApplicationReviewRequestTaskPayload() {
        RequestTaskPayloadType empApplicationReviewRequestTaskPayloadType = RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD;
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .applicationTimeframeInfo(EmpApplicationTimeframeInfo.builder().dateOfStart(LocalDate.now()).submittedOnTime(Boolean.TRUE).build())
            .emissionsMonitoringApproach(FuelMonitoringApproach.builder().build())
            .operatorDetails(EmpOperatorDetails.builder()
                .operatorName("operatorName")
                .crcoCode("crcoCode")
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

        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );

        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "abbreviations", List.of(true),
            "applicationTimeframeInfo", List.of(true),
            "emissionsMonitoringApproach", List.of(true)
        );

        EmpIssuanceUkEtsRequestPayload empIssuanceUkEtsRequestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empSectionsCompleted(empSectionsCompleted)
            .empAttachments(empAttachments)
            .build();

        String operatorName = "anotherOperatorName";
        String crcoCode = "anotherCode";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .crcoCode(crcoCode)
            .serviceContactDetails(serviceContactDetails)
            .build();

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload =
            empUkEtsReviewMapper.toEmpIssuanceUkEtsApplicationReviewRequestTaskPayload(
                empIssuanceUkEtsRequestPayload, aviationAccountInfo, empApplicationReviewRequestTaskPayloadType);

        assertEquals(empApplicationReviewRequestTaskPayloadType,applicationReviewRequestTaskPayload.getPayloadType());
        assertEquals(aviationAccountInfo.getServiceContactDetails(), applicationReviewRequestTaskPayload.getServiceContactDetails());

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

        assertThat(applicationReviewRequestTaskPayload.getReviewSectionsCompleted()).isEmpty();
        assertThat(applicationReviewRequestTaskPayload.getReviewGroupDecisions()).isEmpty();
        assertThat(applicationReviewRequestTaskPayload.getReviewAttachments()).isEmpty();
    }

    @Test
    void toEmpIssuanceUkEtsApplicationApprovedRequestActionPayload() {
        RequestActionPayloadType empApplicationApprovedRequestActionPayloadType = RequestActionPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_APPROVED_PAYLOAD;
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .applicationTimeframeInfo(EmpApplicationTimeframeInfo.builder().dateOfStart(LocalDate.now()).submittedOnTime(Boolean.TRUE).build())
            .emissionsMonitoringApproach(FuelMonitoringApproach.builder().build())
            .operatorDetails(EmpOperatorDetails.builder()
                .operatorName("empOpertorDetailsOperatorName")
                .crcoCode("empOpertorDetailsCrcoCode")
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
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = Map.of(
            EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build(),
            EmpUkEtsReviewGroup.LATE_SUBMISSION, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build(),
            EmpUkEtsReviewGroup.MONITORING_APPROACH, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
        );

        EmpIssuanceUkEtsRequestPayload empIssuanceUkEtsRequestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empAttachments(empAttachments)
            .reviewAttachments(reviewAttachments)
            .determination(determination)
            .decisionNotification(decisionNotification)
            .reviewGroupDecisions(reviewGroupDecisions)
            .build();

        String operatorName = "operatorName";
        String crcoCode = "crcoCode";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .crcoCode(crcoCode)
            .serviceContactDetails(serviceContactDetails)
            .build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of(
            "operatorUserId", RequestActionUserInfo.builder().name("operatorUserName").roleCode("admin").build(),
            "regulatorUserId", RequestActionUserInfo.builder().name("regulatorUserName").roleCode("admin").build()
        );

        EmpIssuanceUkEtsApplicationApprovedRequestActionPayload applicationApprovedRequestActionPayload =
            empUkEtsReviewMapper.toEmpIssuanceUkEtsApplicationApprovedRequestActionPayload(empIssuanceUkEtsRequestPayload, aviationAccountInfo,
                usersInfo, empApplicationApprovedRequestActionPayloadType);

        assertThat(applicationApprovedRequestActionPayload.getPayloadType()).isEqualTo(empApplicationApprovedRequestActionPayloadType);

        EmissionsMonitoringPlanUkEts taskActionPayloadEmpObject = applicationApprovedRequestActionPayload.getEmissionsMonitoringPlan();

        assertEquals(emissionsMonitoringPlan.getAbbreviations(), taskActionPayloadEmpObject.getAbbreviations());
        assertEquals(emissionsMonitoringPlan.getApplicationTimeframeInfo(), taskActionPayloadEmpObject.getApplicationTimeframeInfo());
        assertEquals(emissionsMonitoringPlan.getEmissionsMonitoringApproach(), taskActionPayloadEmpObject.getEmissionsMonitoringApproach());

        EmpOperatorDetails taskActionPayloadEmpObjectOperatorDetails = taskActionPayloadEmpObject.getOperatorDetails();

        assertNotNull(taskActionPayloadEmpObjectOperatorDetails);
        assertEquals(aviationAccountInfo.getCrcoCode(), taskActionPayloadEmpObjectOperatorDetails.getCrcoCode());
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
    void toEmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload() {
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.APPROVED)
            .reason("determination reason")
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operatorUserId"))
            .signatory("regulatorUserId")
            .build();

        EmpIssuanceUkEtsRequestPayload empIssuanceUkEtsRequestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
            .determination(determination)
            .decisionNotification(decisionNotification)
            .build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of(
            "operatorUserId", RequestActionUserInfo.builder().name("operatorUserName").roleCode("admin").build(),
            "regulatorUserId", RequestActionUserInfo.builder().name("regulatorUserName").roleCode("admin").build()
        );

        EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload applicationDeemedWithdrawnRequestActionPayload =
            empUkEtsReviewMapper.toEmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload(empIssuanceUkEtsRequestPayload, usersInfo,
                RequestActionPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD);

        assertEquals(RequestActionPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD,
            applicationDeemedWithdrawnRequestActionPayload.getPayloadType());
        assertEquals(determination, applicationDeemedWithdrawnRequestActionPayload.getDetermination());
        assertEquals(decisionNotification, applicationDeemedWithdrawnRequestActionPayload.getDecisionNotification());
        assertThat(applicationDeemedWithdrawnRequestActionPayload.getUsersInfo()).containsExactlyInAnyOrderEntriesOf(usersInfo);
    }

    @Test
    void toEmissionsMonitoringPlanUkEtsContainer() {
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .applicationTimeframeInfo(EmpApplicationTimeframeInfo.builder().dateOfStart(LocalDate.now()).submittedOnTime(Boolean.TRUE).build())
            .emissionsMonitoringApproach(FuelMonitoringApproach.builder().build())
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

        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();

        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment1",
            UUID.randomUUID(), "attachment2"
        );

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .serviceContactDetails(serviceContactDetails)
                .empAttachments(empAttachments)
                .build();

        EmissionsMonitoringPlanUkEtsContainer empUkEtsContainer = empUkEtsReviewMapper.toEmissionsMonitoringPlanUkEtsContainer(reviewRequestTaskPayload);

        assertEquals(EmissionTradingScheme.UK_ETS_AVIATION, empUkEtsContainer.getScheme());
        assertEquals(emissionsMonitoringPlan, empUkEtsContainer.getEmissionsMonitoringPlan());
        assertEquals(serviceContactDetails, empUkEtsContainer.getServiceContactDetails());
        assertThat(empUkEtsContainer.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
    }
}