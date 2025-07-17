package uk.gov.pmrv.api.verificationbody.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyDTO;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyDetailsQueryServiceTest {

    @InjectMocks
    private VerificationBodyDetailsQueryService service;

    @Mock
    private VerificationBodyQueryService verificationBodyQueryService;

    @Test
    void getVerificationBodyDetails() {
        final Long vbId = 1L;
        final VerificationBodyDTO verificationBodyDTO = VerificationBodyDTO.builder()
                .id(vbId)
                .name("name")
                .accreditationReferenceNumber("accrRefNum")
                .status(VerificationBodyStatus.ACTIVE)
                .address(AddressDTO.builder()
                        .line1("line1")
                        .city("city")
                        .country("GR")
                        .postcode("postcode")
                        .build())
                .emissionTradingSchemes(Set.of(EmissionTradingScheme.CORSIA))
                .build();

        final VerificationBodyDetails expected = VerificationBodyDetails.builder()
                .name(verificationBodyDTO.getName())
                .accreditationReferenceNumber(verificationBodyDTO.getAccreditationReferenceNumber())
                .address(verificationBodyDTO.getAddress())
                .emissionTradingSchemes(verificationBodyDTO.getEmissionTradingSchemes())
                .build();

        when(verificationBodyQueryService.getVerificationBodyOptById(vbId)).thenReturn(Optional.of(verificationBodyDTO));

        // Invoke
        Optional<VerificationBodyDetails> actual = service.getVerificationBodyDetails(vbId);

        // Verify
        verify(verificationBodyQueryService, times(1)).getVerificationBodyOptById(vbId);
        Assertions.assertEquals(expected, actual.get());
    }
    
    @Test
    void getVerificationBodyDetails_not_found() {
        final Long vbId = 1L;

        when(verificationBodyQueryService.getVerificationBodyOptById(vbId)).thenReturn(Optional.empty());

        // Invoke
        Optional<VerificationBodyDetails> actual = service.getVerificationBodyDetails(vbId);

        assertThat(actual).isEmpty();
        
        // Verify
        verify(verificationBodyQueryService, times(1)).getVerificationBodyOptById(vbId);
    }
}
