package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload;

class EmpCorsiaSubmitMapperTest {

    private EmpCorsiaSubmitMapper mapper = Mappers.getMapper(EmpCorsiaSubmitMapper.class);

    @Test
    void toEmissionsMonitoringPlanCorsiaContainer() {
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .build();
        EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
            EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload.builder()
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .build();

        EmissionsMonitoringPlanCorsiaContainer expectedEmpContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .scheme(EmissionTradingScheme.CORSIA)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .build();

        assertEquals(expectedEmpContainer, mapper.toEmissionsMonitoringPlanCorsiaContainer(requestTaskPayload));
    }

    @Test
    void toEmpIssuanceUkEtsApplicationSubmittedRequestActionPayload() {
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .operatorName("empOperatorName")
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
        Map<String, List<Boolean>> empSectionsCompleted = Map.of("Section A", List.of(true));
        Map<UUID, String> empAttachments = Map.of(UUID.randomUUID(), "attachmentName");
        EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
            EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload.builder()
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .empSectionsCompleted(empSectionsCompleted)
                .empAttachments(empAttachments)
                .build();

        String operatorName = "operatorName";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .serviceContactDetails(serviceContactDetails)
            .build();

        EmissionsMonitoringPlanCorsia expectedEmissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .operatorName(operatorName)
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

        EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload expectedRequestActionPayload =
            EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED_PAYLOAD)
                .emissionsMonitoringPlan(expectedEmissionsMonitoringPlan)
                .empSectionsCompleted(empSectionsCompleted)
                .serviceContactDetails(serviceContactDetails)
                .empAttachments(empAttachments)
                .build();

        assertEquals(expectedRequestActionPayload, mapper.toEmpIssuanceCorsiaApplicationSubmittedRequestActionPayload(requestTaskPayload, aviationAccountInfo));
    }
}