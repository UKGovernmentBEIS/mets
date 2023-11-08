package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmpUkEtsMapperTest {

    private final EmpUkEtsMapper empUkEtsMapper = Mappers.getMapper(EmpUkEtsMapper.class);

    @Test
    void toEmissionsMonitoringPlanUkEtsContainer() {
        EmissionTradingScheme scheme = EmissionTradingScheme.UK_ETS_AVIATION;
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
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

        EmpIssuanceUkEtsRequestPayload requestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
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

        //invoke
        EmissionsMonitoringPlanUkEtsContainer emissionsMonitoringPlanUkEtsContainer =
            empUkEtsMapper.toEmissionsMonitoringPlanUkEtsContainer(requestPayload, aviationAccountInfo, scheme);

        assertEquals(scheme, emissionsMonitoringPlanUkEtsContainer.getScheme());
        assertThat(emissionsMonitoringPlanUkEtsContainer.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);

        EmissionsMonitoringPlanUkEts emp = emissionsMonitoringPlanUkEtsContainer.getEmissionsMonitoringPlan();
        EmpOperatorDetails empOperatorDetails = emp.getOperatorDetails();

        assertNotNull(empOperatorDetails);
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getOperatorName(), empOperatorDetails.getOperatorName());
        assertEquals(aviationAccountInfo.getCrcoCode(), empOperatorDetails.getCrcoCode());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getActivitiesDescription(), empOperatorDetails.getActivitiesDescription());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getFlightIdentification(), empOperatorDetails.getFlightIdentification());
    }
}