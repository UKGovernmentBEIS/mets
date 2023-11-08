package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpIssuingAuthorityQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
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
class EmpOperatorDetailsSectionValidatorTest {

    @InjectMocks
    private EmpOperatorDetailsSectionValidator validator;

    @Mock
    private EmpIssuingAuthorityQueryService issuingAuthorityQueryService;

    @Test
    void validate_valid_issuing_authority_when_should_exist() {
        String issuingAuthority = "Greece - Hellenic Civil Aviation Authority";
        String countryCode = "GR";
        String crcoCode = "CRCO code";
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
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
        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();
    }

    @Test
    void validate_valid_when_issuing_authority_should_not_exist() {
        String countryCode = "GR";
        String crcoCode = "CRCO code";
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
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

        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();

        verifyNoInteractions(issuingAuthorityQueryService);
    }

    @Test
    void validate_invalid() {
        String issuingAuthority = "Greece - Hellenic Civil Aviation Authority";
        String countryCode = "GR";
        String invalidIssuingAuthority = "Invalid issuing authority";
        String invalidCountryCode = "Invalid country code";
        String invalidCrcoCode = "Invalid crco code";
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .operatorDetails(EmpOperatorDetails.builder()
                                .crcoCode(invalidCrcoCode)
                                .airOperatingCertificate(AirOperatingCertificate.builder()
                                        .certificateExist(Boolean.TRUE)
                                        .issuingAuthority(issuingAuthority)
                                        .build())
                                .operatingLicense(OperatingLicense.builder()
                                        .licenseExist(Boolean.TRUE)
                                        .issuingAuthority(invalidIssuingAuthority)
                                        .build())
                                .organisationStructure(LimitedCompanyOrganisation.builder()
                                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                                        .organisationLocation(LocationOnShoreStateDTO.builder()
                                                .country(countryCode)
                                                .build())
                                        .differentContactLocationExist(Boolean.TRUE)
                                        .differentContactLocation(LocationOnShoreStateDTO.builder()
                                                .country(invalidCountryCode)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        when(issuingAuthorityQueryService.existsByName(issuingAuthority)).thenReturn(true);
        when(issuingAuthorityQueryService.existsByName(invalidIssuingAuthority)).thenReturn(false);
        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getEmpViolations()).isNotEmpty();
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage).containsOnly(
                EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ISSUING_AUTHORITY.getMessage());
    }
}
