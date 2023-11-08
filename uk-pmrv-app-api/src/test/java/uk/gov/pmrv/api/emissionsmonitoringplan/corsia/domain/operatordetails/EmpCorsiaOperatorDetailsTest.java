package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.service.CountryService;
import uk.gov.pmrv.api.referencedata.service.CountryValidator;

@ExtendWith(MockitoExtension.class)
class EmpCorsiaOperatorDetailsTest {

    private final CountryService countryService = mock(CountryService.class);
    private final List<ConstraintValidator<?,?>> customConstraintValidators =
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
    void validate_operator_details_valid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("registration number")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .evidenceFiles(Set.of(UUID.randomUUID()))
                .differentContactLocationExist(Boolean.TRUE)
                .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_operator_details_invalid_operator_name() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("registration number")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .evidenceFiles(Set.of(UUID.randomUUID()))
                .differentContactLocationExist(Boolean.TRUE)
                .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_operator_details_valid_missing_flight_identification() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("registration number")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .evidenceFiles(Set.of(UUID.randomUUID()))
                .differentContactLocationExist(Boolean.TRUE)
                .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_limited_company_no_registration_number_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .evidenceFiles(Set.of(UUID.randomUUID()))
                .differentContactLocationExist(Boolean.TRUE)
                .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_limited_company_no_registered_address_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("registration number")
                .evidenceFiles(Set.of(UUID.randomUUID()))
                .differentContactLocationExist(Boolean.TRUE)
                .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_limited_company_no_evidence_valid() {
        
        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("registration number")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .evidenceFiles(Set.of())
                .differentContactLocationExist(Boolean.TRUE)
                .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_limited_company_diferent_address_exist_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("registration number")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .evidenceFiles(Set.of())
                .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(2, violations.size());
    }

    @Test
    void validate_limited_company_different_address_false_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("registration number")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .evidenceFiles(Set.of(UUID.randomUUID()))
                .differentContactLocationExist(Boolean.FALSE)
                .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_limited_company_different_address_false_valid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("registration number")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .evidenceFiles(Set.of(UUID.randomUUID()))
                .differentContactLocationExist(Boolean.FALSE)
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_individual_valid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_individual_no_full_name_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);
        assertEquals(1, violations.size());
    }

    @Test
    void validate_individual_no_contact_address_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_partnership_valid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(PartnershipOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                .partnershipName("partnership name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .partners(Set.of("partner1", "partner2"))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_partnership_no_name_valid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(PartnershipOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .partners(Set.of("partner1", "partner2"))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_partnership_no_office_address_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(PartnershipOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                .partnershipName("partnership name")
                .partners(Set.of("partner1", "partner2"))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_partnership_no_partners_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(PartnershipOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                .partnershipName("partnership name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_partnership_blank_partner_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(PartnershipOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                .partnershipName("partnership name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .partners(Set.of("partner1", ""))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();
        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_optional_address_fields_valid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("registration number")
                .organisationLocation(createLocation("line1", null, "city", "GR", null, null, LocationType.ONSHORE))
                .evidenceFiles(Set.of(UUID.randomUUID()))
                .differentContactLocationExist(Boolean.TRUE)
                .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_mandatory_address_fields_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("registration number")
                .organisationLocation(createLocation("line1", null, "city", "GR", null, null, LocationType.ONSHORE))
                .evidenceFiles(Set.of(UUID.randomUUID()))
                .differentContactLocationExist(Boolean.TRUE)
                .differentContactLocation(createLocation(null, null, null, null, "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(3, violations.size());
    }

    @Test
    void validate_aircraft_registration_markings_valid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
                .aircraftRegistrationMarkings(Set.of("marking"))
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_aircraft_registration_markings_no_markings_valid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_icao_no_icao_designators_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_individual_no_certificate_exists_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(4, violations.size());
    }

    @Test
    void validate_no_certiticate_number_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_certificate_issuing_authority_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_certificate_files_not_exist_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_certificate_not_exist_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.FALSE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(3, violations.size());
    }

    @Test
    void validate_certificate_not_exist_valid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.FALSE)
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_operating_certificate_null_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .organisationStructure(IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("full name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_organisation_structure_null_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_operator_type_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(PartnershipOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                .partnershipName("partnership name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .partners(Set.of("partner1", "partner2"))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_flight_types_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(PartnershipOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                .partnershipName("partnership name")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .partners(Set.of("partner1", "partner2"))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .activityDescription("activity description")
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_activity_description_invalid() {

        final EmpCorsiaOperatorDetails operatorDetails = EmpCorsiaOperatorDetails.builder()
            .operatorName("operator name")
            .flightIdentification(FlightIdentification.builder()
                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                .icaoDesignators("BAW,SHT")
                .build())
            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                .certificateExist(Boolean.TRUE)
                .certificateNumber("certificate number")
                .issuingAuthority("Greece - Hellenic Civil Aviation Authority")
                .certificateFiles(Set.of(UUID.randomUUID()))
                .restrictionsExist(Boolean.FALSE)
                .build())
            .organisationStructure(LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("registration number")
                .organisationLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .evidenceFiles(Set.of(UUID.randomUUID()))
                .differentContactLocationExist(Boolean.TRUE)
                .differentContactLocation(createLocation("line1", "line2", "city", "GR", "state", "postcode", LocationType.ONSHORE))
                .build())
            .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .build())
            .subsidiaryCompanyExist(Boolean.FALSE)
            .build();

        final Set<ConstraintViolation<EmpCorsiaOperatorDetails>> violations = validator.validate(operatorDetails);

        assertEquals(1, violations.size());
    }
    
    private LocationOnShoreStateDTO createLocation(String line1, String line2, String city, String country, String state, String postcode, LocationType type) {
        return LocationOnShoreStateDTO.builder()
            .line1(line1)
            .line2(line2)
            .city(city)
            .country(country)
            .state(state)
            .postcode(postcode)
            .type(type)
            .build();
    }
}