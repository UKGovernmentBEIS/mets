package uk.gov.pmrv.api.verificationbody.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyAuthorityServiceTest {

    @InjectMocks
    private VerificationBodyAuthorityService service;

    @Mock
    private VerificationBodyQueryService verificationBodyQueryService;

    @Test
    void getThirdPartyDataProviderId() {
        Long verificationBodyId = 1L;
        Long thirdPartyDataProviderId = 2L;
        VerificationBody verificationBody = VerificationBody.builder().thirdPartyDataProviderId(thirdPartyDataProviderId).id(verificationBodyId).build();
        when(verificationBodyQueryService.getVerificationBodyById(verificationBodyId)).thenReturn(verificationBody);

        Optional<Long> result = service.getThirdPartyDataProviderId(verificationBodyId);

        assertThat(result).isEqualTo(Optional.of(thirdPartyDataProviderId));
        verify(verificationBodyQueryService).getVerificationBodyById(verificationBodyId);
        verifyNoMoreInteractions(verificationBodyQueryService);
    }

}
