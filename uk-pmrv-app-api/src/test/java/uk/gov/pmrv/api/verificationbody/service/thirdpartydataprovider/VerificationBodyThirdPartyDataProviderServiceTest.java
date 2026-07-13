package uk.gov.pmrv.api.verificationbody.service.thirdpartydataprovider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.thirdpartydataprovider.domain.ThirdPartyDataProviderNameInfoDTO;
import uk.gov.netz.api.thirdpartydataprovider.service.ThirdPartyDataProviderQueryService;
import uk.gov.netz.api.thirdpartydataprovider.service.ThirdPartyDataProviderService;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyThirdPartyDataProviderServiceTest {

    @InjectMocks
    private VerificationBodyThirdPartyDataProviderService service;

    @Mock
    private VerificationBodyQueryService verificationBodyQueryService;
    @Mock
    private ThirdPartyDataProviderService thirdPartyDataProviderService;
    @Mock
    private ThirdPartyDataProviderQueryService thirdPartyDataProviderQueryService;

    @Test
    void getThirdPartyDataProviderNameInfoByVerificationBody() {
        Long verificationBodyId = 1L;
        Long thirdPartyDataProviderId = 2L;
        VerificationBody verificationBody = VerificationBody.builder().thirdPartyDataProviderId(thirdPartyDataProviderId).build();
        ThirdPartyDataProviderNameInfoDTO dto = mock(ThirdPartyDataProviderNameInfoDTO.class);

        when(verificationBodyQueryService.getVerificationBodyById(verificationBodyId))
            .thenReturn(verificationBody);
        when(thirdPartyDataProviderService.getThirdPartyDataProviderNameInfoById(thirdPartyDataProviderId))
            .thenReturn(dto);

        Optional<ThirdPartyDataProviderNameInfoDTO> response =
            service.getThirdPartyDataProviderNameInfoByVerificationBody(verificationBodyId);

        assertEquals(Optional.of(dto), response);
        verify(verificationBodyQueryService).getVerificationBodyById(verificationBodyId);
        verify(thirdPartyDataProviderService).getThirdPartyDataProviderNameInfoById(thirdPartyDataProviderId);

        verifyNoMoreInteractions(verificationBodyQueryService, thirdPartyDataProviderService);
        verifyNoMoreInteractions(thirdPartyDataProviderQueryService);
    }

    @Test
    void getAllThirdPartyDataProviders() {
        ThirdPartyDataProviderNameInfoDTO  dto = mock(ThirdPartyDataProviderNameInfoDTO.class);
        when(thirdPartyDataProviderQueryService.getAllThirdPartyDataProviders()).thenReturn(List.of(dto));

        List<ThirdPartyDataProviderNameInfoDTO> allThirdPartyDataProviders = service.getAllThirdPartyDataProviders();

        assertEquals(List.of(dto), allThirdPartyDataProviders);

        verify(thirdPartyDataProviderQueryService).getAllThirdPartyDataProviders();
        verifyNoMoreInteractions(thirdPartyDataProviderQueryService);
        verifyNoInteractions(verificationBodyQueryService,  thirdPartyDataProviderService);
    }
}