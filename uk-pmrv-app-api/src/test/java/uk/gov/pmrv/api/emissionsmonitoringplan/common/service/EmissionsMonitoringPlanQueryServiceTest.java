package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpAccountDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmissionsMonitoringPlanRepository;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.service.FileDocumentService;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionsMonitoringPlanQueryServiceTest {

    @InjectMocks
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private EmissionsMonitoringPlanRepository emissionsMonitoringPlanRepository;

    @Mock
    private FileDocumentService fileDocumentService;


    @Test
    void getEmissionsMonitoringPlanDTOByAccountId() {
        Long accountId = 1L;
        String empId = "empId";
        String fileDocumentId = "fileDocumentId";
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder().build();
        EmissionsMonitoringPlanEntity empEntity = EmissionsMonitoringPlanEntity.builder()
                .id(empId)
                .accountId(accountId)
                .fileDocumentUuid(fileDocumentId)
                .empContainer(empContainer)
                .build();
        when(emissionsMonitoringPlanRepository.findByAccountId(accountId)).thenReturn(Optional.of(empEntity));
        when(fileDocumentService.getFileInfoDTO(fileDocumentId)).thenReturn(FileInfoDTO.builder().build());
        final Optional<EmpDetailsDTO> actual = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanDetailsDTOByAccountId(accountId);
        assertTrue(actual.isPresent());
        assertEquals(empEntity.getId(), actual.get().getId());
    }

    @Test
    void getEmpContainerById() {
        Long accountId = 1L;
        String empId = "EMP_ID-1";
        String fileDocumentId = "fileDocumentId";
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder().build();
        EmissionsMonitoringPlanEntity empEntity = EmissionsMonitoringPlanEntity.builder()
                .id(empId)
                .accountId(accountId)
                .fileDocumentUuid(fileDocumentId)
                .empContainer(empContainer)
                .build();
        when(emissionsMonitoringPlanRepository.findById(empId)).thenReturn(Optional.of(empEntity));

        EmissionsMonitoringPlanContainer result = emissionsMonitoringPlanQueryService.getEmpContainerById(empId);

        assertEquals(empContainer, result);
    }

    @Test
    void validateEmpExistsByIdAndFileDocumentUuid() {
        Long accountId = 1L;
        String empId = "EMP_ID-1";
        String fileDocumentId = "fileDocumentId";
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder().build();
        EmissionsMonitoringPlanEntity empEntity = EmissionsMonitoringPlanEntity.builder()
                .id(empId)
                .accountId(accountId)
                .fileDocumentUuid(fileDocumentId)
                .empContainer(empContainer)
                .build();
        when(emissionsMonitoringPlanRepository.findEmpByIdAndFileDocumentUuid(empId, fileDocumentId)).thenReturn(Optional.of(empEntity));
        EmissionsMonitoringPlanContainer actual = emissionsMonitoringPlanQueryService.getEmpContainerByIdAndFileDocumentUuid(empId, fileDocumentId);
        assertEquals(empContainer, actual);
    }

    @Test
    void getEmpIdByAccountId() {
        Long accountId = 1L;
        String empId = "EMP_ID-1";

        when(emissionsMonitoringPlanRepository.findEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));

        Optional<String> result = emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId);

        assertThat(result).isNotEmpty();
        assertEquals(empId, result.get());
    }

    @Test
    void getEmpIdByAccountId_returns_empty() {
        Long accountId = 1L;

        when(emissionsMonitoringPlanRepository.findEmpIdByAccountId(accountId)).thenReturn(Optional.empty());

        Optional<String> result = emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId);

        assertThat(result).isEmpty();
    }
    
    @Test
    void getEmpAccountsByAccountIds() {
    	Set<Long> accountIds = Set.of(1L, 2L);
    	EmpAccountDTO emp1 = new EmpAccountDTO() {
			@Override
			public String getEmpId() {return "emp1";}
			@Override
			public Long getAccountId() {return 1L;}
		};
		
		EmpAccountDTO emp2 = new EmpAccountDTO() {
			@Override
			public String getEmpId() {return "emp2";}
			@Override
			public Long getAccountId() {return 2L;}
		};
		
    	Set<EmpAccountDTO> empAccountDTOs = Set.of(emp1, emp2);
    	
    	when(emissionsMonitoringPlanRepository.findAllByAccountIdIn(accountIds)).thenReturn(empAccountDTOs);
    	
    	Map<Long, EmpAccountDTO> result = emissionsMonitoringPlanQueryService.getEmpAccountsByAccountIds(accountIds);
    	
    	assertThat(result).containsAllEntriesOf(Map.of(
    			1L, emp1,
    			2L, emp2));
    	
    	verify(emissionsMonitoringPlanRepository, times(1)).findAllByAccountIdIn(accountIds);
    }

    @Test
    void getEmissionsMonitoringPlanUkEtsDTOByAccountId() {
        Long accountId = 1L;
        String empId = "empId";
        String fileDocumentId = "fileDocumentId";
        String operatorName = "operator name";
        String crcoCode = "crco code";
        final FlightIdentification flightIdentification = createFlightIdentification(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION, "icao Designators", Set.of());
        final AirOperatingCertificate airOperatingCertificate = createAirOperatingCertificate(Boolean.TRUE, "certificate number", "issuing authority", Set.of(UUID.randomUUID()));
        final OperatingLicense operatingLicense = createOperatingLicense(Boolean.TRUE, "license number", "issuing authority");
        final LimitedCompanyOrganisation organisationStructure = (LimitedCompanyOrganisation) createOrganisationStructure(OrganisationLegalStatusType.LIMITED_COMPANY);
        final ActivitiesDescription activitiesDescription = createActivitiesDescription();
        EmissionsMonitoringPlanEntity empEntity = EmissionsMonitoringPlanEntity.builder()
                .id(empId)
                .accountId(accountId)
                .fileDocumentUuid(fileDocumentId)
                .empContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                                .operatorDetails(EmpOperatorDetails.builder()
                                        .operatorName(operatorName)
                                        .crcoCode(crcoCode)
                                        .flightIdentification(flightIdentification)
                                        .airOperatingCertificate(airOperatingCertificate)
                                        .operatingLicense(operatingLicense)
                                        .organisationStructure(organisationStructure)
                                        .activitiesDescription(activitiesDescription)
                                        .build())
                                .build())
                        .build())
                .build();
        when(emissionsMonitoringPlanRepository.findByAccountId(accountId)).thenReturn(Optional.of(empEntity));
        final Optional<EmissionsMonitoringPlanUkEtsDTO> actual = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
        assertTrue(actual.isPresent());
        assertEquals(empId, actual.get().getId());
        assertEquals(accountId, actual.get().getAccountId());
        final Optional<EmpOperatorDetails> operatorDetailsOptional = actual.map(EmissionsMonitoringPlanUkEtsDTO::getEmpContainer).map(EmissionsMonitoringPlanUkEtsContainer::getEmissionsMonitoringPlan)
                .map(EmissionsMonitoringPlanUkEts::getOperatorDetails);
        assertTrue(operatorDetailsOptional.isPresent());
        assertEquals(operatorName, operatorDetailsOptional.get().getOperatorName());
        assertEquals(crcoCode, operatorDetailsOptional.get().getCrcoCode());
        assertEquals(flightIdentification, operatorDetailsOptional.get().getFlightIdentification());
        assertEquals(airOperatingCertificate, operatorDetailsOptional.get().getAirOperatingCertificate());
        assertEquals(operatingLicense, operatorDetailsOptional.get().getOperatingLicense());
        assertEquals(organisationStructure, operatorDetailsOptional.get().getOrganisationStructure());
        assertEquals(activitiesDescription, operatorDetailsOptional.get().getActivitiesDescription());
    }

    @Test
    void getEmissionsMonitoringPlanUkEtsDTOByAccountId_no_emp_found() {
        Long accountId = 1L;

        when(emissionsMonitoringPlanRepository.findByAccountId(accountId)).thenReturn(Optional.empty());
        final Optional<EmissionsMonitoringPlanUkEtsDTO> actual = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getEmissionsMonitoringPlanUkEtsDTOByAccountId_missing_operator_details_fields() {
        Long accountId = 1L;
        String empId = "empId";
        String fileDocumentId = "fileDocumentId";
        String operatorName = "operator name";
        String crcoCode = "crco code";
        final FlightIdentification flightIdentification = createFlightIdentification(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION, "icao Designators", Set.of());
        final AirOperatingCertificate airOperatingCertificate = createAirOperatingCertificate(Boolean.TRUE, "certificate number", "issuing authority", Set.of(UUID.randomUUID()));
        final LimitedCompanyOrganisation organisationStructure = (LimitedCompanyOrganisation) createOrganisationStructure(OrganisationLegalStatusType.LIMITED_COMPANY);
        EmissionsMonitoringPlanEntity empEntity = EmissionsMonitoringPlanEntity.builder()
                .id(empId)
                .accountId(accountId)
                .fileDocumentUuid(fileDocumentId)
                .empContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                                .operatorDetails(EmpOperatorDetails.builder()
                                        .operatorName(operatorName)
                                        .crcoCode(crcoCode)
                                        .flightIdentification(flightIdentification)
                                        .airOperatingCertificate(airOperatingCertificate)
                                        .organisationStructure(organisationStructure)
                                        .build())
                                .build())
                        .build())
                .build();
        when(emissionsMonitoringPlanRepository.findByAccountId(accountId)).thenReturn(Optional.of(empEntity));
        final Optional<EmissionsMonitoringPlanUkEtsDTO> actual = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
        assertTrue(actual.isPresent());
        assertEquals(empId, actual.get().getId());
        assertEquals(accountId, actual.get().getAccountId());
        final Optional<EmpOperatorDetails> operatorDetailsOptional = actual.map(EmissionsMonitoringPlanUkEtsDTO::getEmpContainer).map(EmissionsMonitoringPlanUkEtsContainer::getEmissionsMonitoringPlan)
                .map(EmissionsMonitoringPlanUkEts::getOperatorDetails);
        assertTrue(operatorDetailsOptional.isPresent());
        assertEquals(operatorName, operatorDetailsOptional.get().getOperatorName());
        assertEquals(crcoCode, operatorDetailsOptional.get().getCrcoCode());
        assertEquals(flightIdentification, operatorDetailsOptional.get().getFlightIdentification());
        assertEquals(airOperatingCertificate, operatorDetailsOptional.get().getAirOperatingCertificate());
        assertEquals(organisationStructure, operatorDetailsOptional.get().getOrganisationStructure());
        assertNull(operatorDetailsOptional.get().getActivitiesDescription());
        assertNull(operatorDetailsOptional.get().getOperatingLicense());
    }

    @Test
    void getEmissionsMonitoringPlanUkEtsDTOByAccountId_individual_organisation() {
        Long accountId = 1L;
        String empId = "empId";
        String fileDocumentId = "fileDocumentId";
        String operatorName = "operator name";
        String crcoCode = "crco code";
        final FlightIdentification flightIdentification = createFlightIdentification(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION, "icao Designators", Set.of());
        final AirOperatingCertificate airOperatingCertificate = createAirOperatingCertificate(Boolean.TRUE, "certificate number", "issuing authority", Set.of(UUID.randomUUID()));
        final OperatingLicense operatingLicense = createOperatingLicense(Boolean.TRUE, "license number", "issuing authority");
        final IndividualOrganisation organisationStructure = (IndividualOrganisation) createOrganisationStructure(OrganisationLegalStatusType.INDIVIDUAL);
        final ActivitiesDescription activitiesDescription = createActivitiesDescription();
        EmissionsMonitoringPlanEntity empEntity = EmissionsMonitoringPlanEntity.builder()
                .id(empId)
                .accountId(accountId)
                .fileDocumentUuid(fileDocumentId)
                .empContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                                .operatorDetails(EmpOperatorDetails.builder()
                                        .operatorName(operatorName)
                                        .crcoCode(crcoCode)
                                        .flightIdentification(flightIdentification)
                                        .airOperatingCertificate(airOperatingCertificate)
                                        .operatingLicense(operatingLicense)
                                        .organisationStructure(organisationStructure)
                                        .activitiesDescription(activitiesDescription)
                                        .build())
                                .build())
                        .build())
                .build();
        when(emissionsMonitoringPlanRepository.findByAccountId(accountId)).thenReturn(Optional.of(empEntity));
        final Optional<EmissionsMonitoringPlanUkEtsDTO> actual = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
        assertTrue(actual.isPresent());
        assertEquals(empId, actual.get().getId());
        assertEquals(accountId, actual.get().getAccountId());
        final Optional<EmpOperatorDetails> operatorDetailsOptional = actual.map(EmissionsMonitoringPlanUkEtsDTO::getEmpContainer).map(EmissionsMonitoringPlanUkEtsContainer::getEmissionsMonitoringPlan)
                .map(EmissionsMonitoringPlanUkEts::getOperatorDetails);
        assertTrue(operatorDetailsOptional.isPresent());
        assertEquals(operatorName, operatorDetailsOptional.get().getOperatorName());
        assertEquals(crcoCode, operatorDetailsOptional.get().getCrcoCode());
        assertEquals(flightIdentification, operatorDetailsOptional.get().getFlightIdentification());
        assertEquals(airOperatingCertificate, operatorDetailsOptional.get().getAirOperatingCertificate());
        assertEquals(operatingLicense, operatorDetailsOptional.get().getOperatingLicense());
        assertEquals(organisationStructure, operatorDetailsOptional.get().getOrganisationStructure());
        assertEquals(activitiesDescription, operatorDetailsOptional.get().getActivitiesDescription());
    }
    
    @Test
    void getEmissionsMonitoringPlanConsolidationNumberByAccountId() {
    	Long accountId = 1L;
    	EmissionsMonitoringPlanEntity empEntity = new EmissionsMonitoringPlanEntity("1", accountId, EmissionsMonitoringPlanUkEtsContainer.builder().build(), null);
    	
    	when(emissionsMonitoringPlanRepository.findByAccountId(accountId)).thenReturn(Optional.of(empEntity));
    	
    	int result = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(accountId);
    	assertThat(result).isEqualTo(1);
    	verify(emissionsMonitoringPlanRepository, times(1)).findByAccountId(accountId);
    }

    private FlightIdentification createFlightIdentification(FlightIdentificationType flightIdentificationType, String icaoDesignators,
                                                            Set<String> registrationMarkings) {
        return FlightIdentification.builder()
                .flightIdentificationType(flightIdentificationType)
                .icaoDesignators(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION.equals(flightIdentificationType) ? icaoDesignators : null)
                .aircraftRegistrationMarkings(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS.equals(flightIdentificationType) ? registrationMarkings : null)
                .build();
    }

    private AirOperatingCertificate createAirOperatingCertificate(Boolean exists, String certificateNumber, String issuingAuthority, Set<UUID> files) {
        return AirOperatingCertificate.builder()
                .certificateExist(exists)
                .certificateNumber(exists ? certificateNumber : null)
                .issuingAuthority(exists ? issuingAuthority : null)
                .certificateFiles(exists ? files : null)
                .build();
    }

    private OperatingLicense createOperatingLicense(Boolean exist, String licenseNumber, String issuingAuthority) {
        return OperatingLicense.builder()
                .licenseExist(exist)
                .licenseNumber(exist ? licenseNumber : null)
                .issuingAuthority(exist ? issuingAuthority : null)
                .build();
    }

    private ActivitiesDescription createActivitiesDescription() {
        return ActivitiesDescription.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                .activityDescription("activity description")
                .build();
    }

    private OrganisationStructure createOrganisationStructure(OrganisationLegalStatusType legalStatusType) {
        if (OrganisationLegalStatusType.LIMITED_COMPANY.equals(legalStatusType)) {
            return LimitedCompanyOrganisation.builder()
                    .legalStatusType(legalStatusType)
                    .registrationNumber("registration name")
                    .organisationLocation(createOrganisationLocation())
                    .differentContactLocationExist(Boolean.FALSE)
                    .evidenceFiles(Set.of(UUID.randomUUID()))
                    .build();
        } else if (OrganisationLegalStatusType.INDIVIDUAL.equals(legalStatusType)) {
            return IndividualOrganisation.builder()
                    .legalStatusType(legalStatusType)
                    .fullName("full name")
                    .organisationLocation(createOrganisationLocation())
                    .build();
        } else {
            return PartnershipOrganisation.builder()
                    .legalStatusType(legalStatusType)
                    .partnershipName("partnership name")
                    .partners(Set.of("partner1", "partner2"))
                    .organisationLocation(createOrganisationLocation())
                    .build();
        }
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
