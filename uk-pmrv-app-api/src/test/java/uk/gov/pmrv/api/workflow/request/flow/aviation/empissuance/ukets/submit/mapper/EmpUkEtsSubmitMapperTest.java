package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmpUkEtsSubmitMapperTest {

    private EmpUkEtsSubmitMapper mapper = Mappers.getMapper(EmpUkEtsSubmitMapper.class);

    @Test
    void toEmissionsMonitoringPlanUkEtsContainer() {
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .build();
        EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
            EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload.builder()
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .build();

        EmissionsMonitoringPlanUkEtsContainer expectedEmpContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
            .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .build();

        assertEquals(expectedEmpContainer, mapper.toEmissionsMonitoringPlanUkEtsContainer(requestTaskPayload));
    }

    @Test
    void toEmpIssuanceUkEtsApplicationSubmittedRequestActionPayload() {
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .operatorDetails(EmpOperatorDetails.builder()
                .operatorName("empOperatorName")
                .crcoCode("empCrcoCode")
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
        Map<String, List<Boolean>> empSectionsCompleted = Map.of("Section A", List.of(true));
        Map<UUID, String> empAttachments = Map.of(UUID.randomUUID(), "attachmentName");
        EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
            EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload.builder()
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .empSectionsCompleted(empSectionsCompleted)
                .empAttachments(empAttachments)
                .build();

        String operatorName = "operatorName";
        String crcoCode = "crcoCode";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("name").email("email").build();
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .crcoCode(crcoCode)
            .serviceContactDetails(serviceContactDetails)
            .build();

        EmissionsMonitoringPlanUkEts expectedEmissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .operatorDetails(EmpOperatorDetails.builder()
                .operatorName(operatorName)
                .crcoCode(crcoCode)
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

        EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload expectedRequestActionPayload =
            EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED_PAYLOAD)
                .emissionsMonitoringPlan(expectedEmissionsMonitoringPlan)
                .empSectionsCompleted(empSectionsCompleted)
                .serviceContactDetails(serviceContactDetails)
                .empAttachments(empAttachments)
                .build();

        assertEquals(expectedRequestActionPayload, mapper.toEmpIssuanceUkEtsApplicationSubmittedRequestActionPayload(requestTaskPayload, aviationAccountInfo));
    }
}