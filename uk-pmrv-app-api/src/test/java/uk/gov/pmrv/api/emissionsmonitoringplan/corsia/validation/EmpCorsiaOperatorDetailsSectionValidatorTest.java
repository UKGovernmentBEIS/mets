package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpIssuingAuthorityQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AirOperatingCertificateCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.SubsidiaryCompanyCorsia;

@ExtendWith(MockitoExtension.class)
class EmpCorsiaOperatorDetailsSectionValidatorTest {

    @InjectMocks
    private EmpCorsiaOperatorDetailsSectionValidator validator;

    @Mock
    private EmpIssuingAuthorityQueryService issuingAuthorityQueryService;

    @Test
    void validate_valid_issuing_authority_when_should_exist() {
        String issuingAuthority = "Greece - Hellenic Civil Aviation Authority";
        EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .operatorDetails(EmpCorsiaOperatorDetails.builder()
                    .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                        .certificateExist(Boolean.TRUE)
                        .issuingAuthority(issuingAuthority)
                        .build())
                    .subsidiaryCompanyExist(Boolean.TRUE)
                    .subsidiaryCompanies(List.of(SubsidiaryCompanyCorsia.builder()
                        .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                            .certificateExist(Boolean.TRUE)
                            .issuingAuthority(issuingAuthority)
                            .build())
                        .build()))
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
        EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .operatorDetails(EmpCorsiaOperatorDetails.builder()
                    .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                        .certificateExist(Boolean.FALSE)
                        .build())
                    .subsidiaryCompanyExist(Boolean.TRUE)
                    .subsidiaryCompanies(List.of(SubsidiaryCompanyCorsia.builder()
                        .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                            .certificateExist(Boolean.FALSE)
                            .build())
                        .build()))
                    .build())
                .build())
            .build();

        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();

        verifyNoInteractions(issuingAuthorityQueryService);
    }

    @Test
    void validate_valid_issuing_authority_for_subsidiary_when_should_exist() {
        String issuingAuthority = "Greece - Hellenic Civil Aviation Authority";
        EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .operatorDetails(EmpCorsiaOperatorDetails.builder()
                    .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                        .certificateExist(Boolean.FALSE)
                        .build())
                    .subsidiaryCompanyExist(Boolean.TRUE)
                    .subsidiaryCompanies(List.of(SubsidiaryCompanyCorsia.builder()
                        .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                            .certificateExist(Boolean.TRUE)
                            .issuingAuthority(issuingAuthority)
                            .build())
                        .build()))
                    .build())
                .build())
            .build();

        when(issuingAuthorityQueryService.existsByName(issuingAuthority)).thenReturn(true);
        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();
    }

    @Test
    void validate_valid_when_subsidiary_does_not_exist() {
        EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .operatorDetails(EmpCorsiaOperatorDetails.builder()
                    .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                        .certificateExist(Boolean.FALSE)
                        .build())
                    .subsidiaryCompanyExist(Boolean.FALSE)
                    .subsidiaryCompanies(Collections.emptyList())
                    .build())
                .build())
            .build();

        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();

        verifyNoInteractions(issuingAuthorityQueryService);
    }
}
