package uk.gov.pmrv.api.emissionsmonitoringplan.common.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmpEmissionsMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.SmallEmittersMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmissionsMonitoringPlanMapperTest {

    private final EmissionsMonitoringPlanMapper mapper = Mappers.getMapper(EmissionsMonitoringPlanMapper.class);

    @Test
    void toEmissionsMonitoringPlanUkEtsDTO() {

        String empId = "empId";
        Long accountId = 1L;
        final EmpOperatorDetails operatorDetails = createOperatorDetails();
        final EmpEmissionsMonitoringApproach monitoringApproach = createMonitoringApproach();
        final EmpEmissionSources emissionSources = createEmissionSources();

        final EmissionsMonitoringPlanEntity empEntity = EmissionsMonitoringPlanEntity.builder()
                .id(empId)
                .accountId(accountId)
                .empContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                                .emissionsMonitoringApproach(monitoringApproach)
                                .emissionSources(emissionSources)
                                .operatorDetails(operatorDetails)
                                .build())
                        .build())
                .build();
        EmissionsMonitoringPlanUkEtsDTO actual = mapper.toEmissionsMonitoringPlanUkEtsDTO(empEntity);

        final EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = actual.getEmpContainer().getEmissionsMonitoringPlan();

        assertThat(actual.getId()).isEqualTo(empId);
        assertThat(actual.getAccountId()).isEqualTo(accountId);
        assertThat(emissionsMonitoringPlan.getEmissionsMonitoringApproach()).isEqualTo(monitoringApproach);
        assertThat(emissionsMonitoringPlan.getEmissionSources()).isEqualTo(emissionSources);
        assertThat(emissionsMonitoringPlan.getOperatorDetails()).isEqualTo(operatorDetails);
    }

    private EmpEmissionsMonitoringApproach createMonitoringApproach() {
        return SmallEmittersMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                .explanation("explanation")
                .supportingEvidenceFiles(Set.of(UUID.randomUUID()))
                .build();
    }

    private EmpEmissionSources createEmissionSources() {
        return EmpEmissionSources.builder()
                .aircraftTypes(Set.of(AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .manufacturer("manufacturer")
                                        .model("model")
                                        .designatorType("designator")
                                        .build())
                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                .fuelTypes(List.of(FuelType.JET_GASOLINE))
                                .numberOfAircrafts(10L)
                                .subtype("subtype")
                        .build()))
                .build();
    }

    private EmpOperatorDetails createOperatorDetails() {
        return EmpOperatorDetails.builder()
                .operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("icao designators")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("issuing authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("issuing authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .partnershipName("partnership name")
                        .partners(Set.of("partner"))
                        .organisationLocation(LocationOnShoreStateDTO.builder()
                                .type(LocationType.ONSHORE_STATE)
                                .line1("line 1")
                                .city("city")
                                .country("GR")
                                .build())
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();
    }
}
