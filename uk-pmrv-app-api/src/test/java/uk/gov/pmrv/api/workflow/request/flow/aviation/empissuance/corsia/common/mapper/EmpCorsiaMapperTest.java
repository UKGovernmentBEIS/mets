package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;

class EmpCorsiaMapperTest {

    private final EmpCorsiaMapper empCorsiaMapper = Mappers.getMapper(EmpCorsiaMapper.class);

    @Test
    void toEmissionsMonitoringPlanCorsiaContainer() {
        EmissionTradingScheme scheme = EmissionTradingScheme.CORSIA;
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
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

        EmpIssuanceCorsiaRequestPayload requestPayload = EmpIssuanceCorsiaRequestPayload.builder()
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
        EmissionsMonitoringPlanCorsiaContainer emissionsMonitoringPlanCorsiaContainer =
            empCorsiaMapper.toEmissionsMonitoringPlanCorsiaContainer(requestPayload, aviationAccountInfo, scheme);

        assertEquals(scheme, emissionsMonitoringPlanCorsiaContainer.getScheme());
        assertThat(emissionsMonitoringPlanCorsiaContainer.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);

        EmissionsMonitoringPlanCorsia emp = emissionsMonitoringPlanCorsiaContainer.getEmissionsMonitoringPlan();
        EmpCorsiaOperatorDetails empOperatorDetails = emp.getOperatorDetails();

        assertNotNull(empOperatorDetails);
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getOperatorName(), empOperatorDetails.getOperatorName());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getActivitiesDescription(), empOperatorDetails.getActivitiesDescription());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getFlightIdentification(), empOperatorDetails.getFlightIdentification());
    }
}