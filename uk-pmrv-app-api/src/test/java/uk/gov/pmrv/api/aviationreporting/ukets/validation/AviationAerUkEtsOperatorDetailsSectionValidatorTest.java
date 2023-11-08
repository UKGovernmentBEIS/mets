package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpIssuingAuthorityQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsOperatorDetailsSectionValidatorTest {

    @InjectMocks
    private AviationAerUkEtsOperatorDetailsSectionValidator validator;

    @Mock
    private EmpIssuingAuthorityQueryService issuingAuthorityQueryService;

    @Test
    void validate_valid_issuing_authority_when_should_exist() {
        String issuingAuthority = "Greece - Hellenic Civil Aviation Authority";
        String countryCode = "GR";
        String crcoCode = "CRCO code";
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .operatorDetails(EmpOperatorDetails.builder()
                                .crcoCode(crcoCode)
                                .airOperatingCertificate(AirOperatingCertificate.builder()
                                        .certificateExist(Boolean.TRUE)
                                        .issuingAuthority(issuingAuthority)
                                        .build())
                                .operatingLicense(OperatingLicense.builder()
                                        .licenseExist(Boolean.TRUE)
                                        .issuingAuthority(issuingAuthority)
                                        .build())
                                .organisationStructure(LimitedCompanyOrganisation.builder()
                                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                                        .organisationLocation(LocationOnShoreStateDTO.builder()
                                                .country(countryCode)
                                                .build())
                                        .differentContactLocationExist(Boolean.FALSE)
                                        .build())
                                .build())
                        .build())
                .build();

        when(issuingAuthorityQueryService.existsByName(issuingAuthority)).thenReturn(true);
        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void validate_valid_when_issuing_authority_should_not_exist() {
        String countryCode = "GR";
        String crcoCode = "CRCO code";
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
            .aer(AviationAerUkEts.builder()
                .operatorDetails(EmpOperatorDetails.builder()
                    .crcoCode(crcoCode)
                    .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(Boolean.FALSE)
                        .build())
                    .operatingLicense(OperatingLicense.builder()
                        .licenseExist(Boolean.FALSE)
                        .build())
                    .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .organisationLocation(LocationOnShoreStateDTO.builder()
                            .country(countryCode)
                            .build())
                        .differentContactLocationExist(Boolean.FALSE)
                        .build())
                    .build())
                .build())
            .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();

        verifyNoInteractions(issuingAuthorityQueryService);
    }

    @Test
    void validate_invalid() {
        String issuingAuthority = "Greece - Hellenic Civil Aviation Authority";
        String invalidIssuingAuthority = "invalid issuing authority";
        String countryCode = "GR";
        String crcoCode = "CRCO code";
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .operatorDetails(EmpOperatorDetails.builder()
                                .crcoCode(crcoCode)
                                .airOperatingCertificate(AirOperatingCertificate.builder()
                                        .certificateExist(Boolean.TRUE)
                                        .issuingAuthority(invalidIssuingAuthority)
                                        .build())
                                .operatingLicense(OperatingLicense.builder()
                                        .licenseExist(Boolean.TRUE)
                                        .issuingAuthority(issuingAuthority)
                                        .build())
                                .organisationStructure(LimitedCompanyOrganisation.builder()
                                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                                        .organisationLocation(LocationOnShoreStateDTO.builder()
                                                .country(countryCode)
                                                .build())
                                        .differentContactLocationExist(Boolean.FALSE)
                                        .build())
                                .build())
                        .build())
                .build();

        when(issuingAuthorityQueryService.existsByName(issuingAuthority)).thenReturn(true);
        when(issuingAuthorityQueryService.existsByName(invalidIssuingAuthority)).thenReturn(false);
        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_ISSUING_AUTHORITY.getMessage());
    }
}
