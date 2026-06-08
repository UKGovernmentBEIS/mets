package uk.gov.pmrv.api.verificationbody.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyEmissionSchemeRepository;

@ExtendWith(MockitoExtension.class)
class AccreditationRefNumValidationServiceTest {

    @InjectMocks
    private AccreditationRefNumValidationService accreditationRefNumValidationService;

    @Mock
    private VerificationBodyEmissionSchemeRepository verificationBodyEmissionSchemeRepository;

    @Test
    void validate() {
        String accreditationReferenceNumber = "accreditationReferenceNumber";

        when(verificationBodyEmissionSchemeRepository
                .existsByAccreditationReferenceNumber(accreditationReferenceNumber))
                .thenReturn(false);

        accreditationRefNumValidationService.validateCreate(accreditationReferenceNumber);

        verify(verificationBodyEmissionSchemeRepository, times(1))
                .existsByAccreditationReferenceNumber(accreditationReferenceNumber);
    }

    @Test
    void validate_invalid_accreditation_ref_num() {
        String accreditationReferenceNumber = "accreditationReferenceNumber";

        when(verificationBodyEmissionSchemeRepository
                .existsByAccreditationReferenceNumber(accreditationReferenceNumber))
                .thenReturn(true);

        BusinessException be = assertThrows(BusinessException.class, () ->
                accreditationRefNumValidationService.validateCreate(accreditationReferenceNumber));

        assertThat(be.getErrorCode())
                .isEqualTo(ErrorCode.VERIFICATION_BODY_CONTAINS_NON_UNIQUE_REF_NUM);

        verify(verificationBodyEmissionSchemeRepository, times(1))
                .existsByAccreditationReferenceNumber(accreditationReferenceNumber);
    }

    @Test
    void validate_with_vb() {
        Long verificationBodyId = 1L;
        String accreditationReferenceNumber = "accreditationReferenceNumber";

        when(verificationBodyEmissionSchemeRepository
                .existsByAccreditationReferenceNumberAndVerificationBodyIdNot(accreditationReferenceNumber, verificationBodyId))
                .thenReturn(false);

        accreditationRefNumValidationService.validateUpdate(accreditationReferenceNumber, verificationBodyId);

        verify(verificationBodyEmissionSchemeRepository, times(1))
                .existsByAccreditationReferenceNumberAndVerificationBodyIdNot(accreditationReferenceNumber, verificationBodyId);
    }
}