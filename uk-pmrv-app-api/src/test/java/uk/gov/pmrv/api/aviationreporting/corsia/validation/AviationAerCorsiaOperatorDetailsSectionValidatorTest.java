package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpIssuingAuthorityQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AirOperatingCertificateCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaOperatorDetailsSectionValidatorTest {

    @InjectMocks
    private AviationAerCorsiaOperatorDetailsSectionValidator validator;

    @Mock
    private EmpIssuingAuthorityQueryService issuingAuthorityQueryService;

    @Test
    void validate_valid_issuing_authority_when_should_exist() {
        
        final String issuingAuthority = "Greece - Hellenic Civil Aviation Authority";
        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
            .aer(AviationAerCorsia.builder().operatorDetails(AviationCorsiaOperatorDetails.builder()
                    .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                        .certificateExist(Boolean.TRUE)
                        .issuingAuthority(issuingAuthority)
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
    
    void validate_invalid_when_issuing_authority_should_not_exist() {

        final String issuingAuthority = "Greece - Hellenic Civil Aviation Authority";
        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
            .aer(AviationAerCorsia.builder().operatorDetails(AviationCorsiaOperatorDetails.builder()
                    .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                        .certificateExist(Boolean.TRUE)
                        .issuingAuthority(issuingAuthority)
                        .build())
                    .build())
                .build())
            .build();

        when(issuingAuthorityQueryService.existsByName(issuingAuthority)).thenReturn(false);

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        
        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations().get(0).getMessage())
            .isEqualTo(AviationAerViolation.AviationAerViolationMessage.INVALID_ISSUING_AUTHORITY.getMessage());

        verify(issuingAuthorityQueryService, times(1)).existsByName(issuingAuthority);
    }
}
