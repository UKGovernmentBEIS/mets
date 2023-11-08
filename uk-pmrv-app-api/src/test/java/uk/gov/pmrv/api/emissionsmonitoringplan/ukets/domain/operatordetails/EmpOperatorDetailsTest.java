package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.CustomLocalValidatorFactoryBean;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.referencedata.service.CountryValidator;
import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.service.CountryService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpOperatorDetailsTest {

    private CountryService countryService = mock(CountryService.class);
    private List<ConstraintValidator<?,?>> customConstraintValidators =
            Collections.singletonList(new CountryValidator(countryService));
    private Validator validator;


    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = new CustomLocalValidatorFactoryBean(customConstraintValidators)) {
            validator = factory.getValidator();
        }
        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));
    }

    @Test
    void validate_limited_company_valid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .registrationNumber("registration number")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .evidenceFiles(Set.of(UUID.randomUUID()))
                        .differentContactLocationExist(Boolean.TRUE)
                        .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_limited_company_no_registration_number_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .evidenceFiles(Set.of(UUID.randomUUID()))
                        .differentContactLocationExist(Boolean.TRUE)
                        .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_limited_company_no_registered_address_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .registrationNumber("registration number")
                        .evidenceFiles(Set.of(UUID.randomUUID()))
                        .differentContactLocationExist(Boolean.TRUE)
                        .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_limited_company_no_evidence_valid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .registrationNumber("registration number")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .evidenceFiles(Set.of())
                        .differentContactLocationExist(Boolean.TRUE)
                        .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_limited_company_diferent_address_exist_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .registrationNumber("registration number")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .evidenceFiles(Set.of(UUID.randomUUID()))
                        .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(2, violations.size());
    }

    @Test
    void validate_limited_company_different_address_false_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .registrationNumber("registration number")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .evidenceFiles(Set.of(UUID.randomUUID()))
                        .differentContactLocationExist(Boolean.FALSE)
                        .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_limited_company_different_address_false_valid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .registrationNumber("registration number")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .evidenceFiles(Set.of(UUID.randomUUID()))
                        .differentContactLocationExist(Boolean.FALSE)
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_individual_valid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_individual_no_full_name_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_individual_no_contact_address_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_individual_no_legal_status_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_partnership_valid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_partnership_no_name_valid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_partnership_no_office_address_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_partnership_no_partners_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_partnership_blank_partner_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", ""))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();
        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_operator_name_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder()
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .registrationNumber("registration number")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .evidenceFiles(Set.of(UUID.randomUUID()))
                        .differentContactLocationExist(Boolean.TRUE)
                        .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_crco_code_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder()
                .operatorName("operator name")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .registrationNumber("registration number")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .evidenceFiles(Set.of(UUID.randomUUID()))
                        .differentContactLocationExist(Boolean.TRUE)
                        .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_optional_address_fields_valid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .registrationNumber("registration number")
                        .organisationLocation(createLocation("line1", null, "city", "GR", null, null))
                        .evidenceFiles(Set.of(UUID.randomUUID()))
                        .differentContactLocationExist(Boolean.TRUE)
                        .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_mandatory_address_fields_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .registrationNumber("registration number")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .evidenceFiles(Set.of(UUID.randomUUID()))
                        .differentContactLocationExist(Boolean.TRUE)
                        .differentContactLocation(createLocation(null, null, null, null, "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(3, violations.size());
    }

    @Test
    void validate_aircraft_registration_markings_valid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
                        .aircraftRegistrationMarkings(Set.of("marking"))
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_aircraft_registration_markings_no_markings_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_icao_no_icao_designators_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_individual_no_certificate_exists_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(4, violations.size());
    }

    @Test
    void validate_no_certiticate_number_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_certificate_issuing_authority_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_certificate_files_not_exist_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_certificate_not_exist_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.FALSE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(3, violations.size());
    }

    @Test
    void validate_certificate_not_exist_valid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.FALSE)
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_operating_certificate_null_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_license_exist_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(3, violations.size());
    }

    @Test
    void validate_no_license_number_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_license_issuing_authority_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_license_not_exist_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.FALSE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(2, violations.size());
    }

    @Test
    void validate_license_not_exist_valid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.FALSE)
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_license_is_null_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_organisation_structure_null_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_operator_type_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_flight_types_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_operator_scopes_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .activityDescription("activity description")
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_activity_description_invalid() {

        final EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder().
                operatorName("operator name")
                .crcoCode("crco code")
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("BAW,SHT")
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.TRUE)
                        .certificateNumber("certificate number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .certificateFiles(Set.of(UUID.randomUUID()))
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.TRUE)
                        .licenseNumber("license number")
                        .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                        .build())
                .organisationStructure(PartnershipOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                        .partnershipName("partnership name")
                        .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode"))
                        .partners(Set.of("partner1", "partner2"))
                        .build())
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType(OperatorType.NON_COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .operationScopes(Set.of(OperationScope.UK_TO_EEA_COUNTRIES))
                        .build())
                .build();

        final Set<ConstraintViolation<EmpOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    private LocationOnShoreStateDTO createLocation(String line1, String line2, String city, String country, String state, String postcode) {

        return LocationOnShoreStateDTO.builder()
                .type(LocationType.ONSHORE_STATE)
                .line1(line1)
                .line2(line2)
                .city(city)
                .country(country)
                .state(state)
                .postcode(postcode)
                .build();
    }

}
