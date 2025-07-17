package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.EmpCorsiaOriginatedData;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AirOperatingCertificateCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaBuildEmpOriginatedDataServiceTest {

	@InjectMocks
    private AviationAerCorsiaBuildEmpOriginatedDataService cut;
    
    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    
    @Mock
    private AviationAccountQueryService aviationAccountQueryService;
    
    @Test
    void build_emp_exists() {
        Long accountId = 1L;
        
        UUID orgStructureEvidenceFile = UUID.randomUUID();
        UUID airOperatingCertificateFile = UUID.randomUUID();
        Map<UUID,String> empAttachments = Map.of(
            orgStructureEvidenceFile, "orgStructureEvidenceFileName",
            airOperatingCertificateFile, "airOperatingCertificateFileName",
            UUID.randomUUID(), "anotherFileName");
        
        EmissionsMonitoringPlanCorsiaDTO emp = EmissionsMonitoringPlanCorsiaDTO.builder()
                .empContainer(EmissionsMonitoringPlanCorsiaContainer.builder()
                    .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                        .operatorDetails(EmpCorsiaOperatorDetails.builder()
                            .operatorName("operator name")
                            .flightIdentification(createFlightIdentification(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION, "icao designators", Set.of()))
                            .airOperatingCertificate(createAirOperatingCertificate(Boolean.TRUE, "certificate number", "issuing authority", Set.of(airOperatingCertificateFile)))
                            .organisationStructure(createLimitedCompanyOrganisationStructure(orgStructureEvidenceFile))
                            .build())
                        .build())
                    .empAttachments(empAttachments)
                    .build())
                .build();
        
		when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId))
				.thenReturn(Optional.of(emp));
		
		AviationAccountInfoDTO accountInfo = AviationAccountInfoDTO.builder().name("accountName").build();
		 when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(accountInfo);
		
		EmpCorsiaOriginatedData expectedResult = EmpCorsiaOriginatedData.builder()
				.operatorDetails(AviationCorsiaOperatorDetails.builder()
                        .operatorName("accountName")
                        .flightIdentification(createFlightIdentification(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION, "icao designators", Set.of()))
                        .airOperatingCertificate(createAirOperatingCertificate(Boolean.TRUE, "certificate number", "issuing authority", Set.of(airOperatingCertificateFile)))
                        .organisationStructure(createLimitedCompanyOrganisationStructure(orgStructureEvidenceFile))
                        .build())
				.operatorDetailsAttachments(Map.of(orgStructureEvidenceFile, "orgStructureEvidenceFileName",
						airOperatingCertificateFile, "airOperatingCertificateFileName"))
				.build();

		
		EmpCorsiaOriginatedData actualResult = cut.build(accountId);
		
		assertThat(expectedResult).isEqualTo(actualResult);
		
		verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
    }
    
    @Test
    void build_no_emp_found() {
        Long accountId = 1L;
        
		when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId))
				.thenReturn(Optional.empty());
		
		AviationAccountInfoDTO accountInfo = AviationAccountInfoDTO.builder().name("accountName").build();
		 when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(accountInfo);
		
		EmpCorsiaOriginatedData expectedResult = EmpCorsiaOriginatedData.builder()
				.operatorDetails(AviationCorsiaOperatorDetails.builder()
						.operatorName("accountName")
						.build())
				.build();

		
		EmpCorsiaOriginatedData actualResult = cut.build(accountId);
		
		assertThat(expectedResult).isEqualTo(actualResult);
		
		verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
    }

    private FlightIdentification createFlightIdentification(FlightIdentificationType flightIdentificationType, String icaoDesignators,
                                                            Set<String> registrationMarkings) {
        return FlightIdentification.builder()
                .flightIdentificationType(flightIdentificationType)
                .icaoDesignators(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION.equals(flightIdentificationType) ? icaoDesignators : null)
                .aircraftRegistrationMarkings(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS.equals(flightIdentificationType) ? registrationMarkings : null)
                .build();
    }

    private AirOperatingCertificateCorsia createAirOperatingCertificate(Boolean exists, String certificateNumber, String issuingAuthority, Set<UUID> files) {
        return AirOperatingCertificateCorsia.builder()
                .certificateExist(exists)
                .certificateNumber(exists ? certificateNumber : null)
                .issuingAuthority(exists ? issuingAuthority : null)
                .certificateFiles(exists ? files : null)
                .build();
    }

    private OrganisationStructure createLimitedCompanyOrganisationStructure(UUID evidenceFile) {
            return LimitedCompanyOrganisation.builder()
                    .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                    .registrationNumber("registration name")
                    .organisationLocation(createOrganisationLocation())
                    .differentContactLocationExist(Boolean.FALSE)
                    .evidenceFiles(Set.of(evidenceFile))
                    .build();
    }

    private LocationOnShoreStateDTO createOrganisationLocation() {
        return LocationOnShoreStateDTO.builder()
                .type(LocationType.ONSHORE_STATE)
                .line1("line 1")
                .country("GR")
                .city("city")
                .state("state")
                .postcode("postcode")
                .build();
    }

}
