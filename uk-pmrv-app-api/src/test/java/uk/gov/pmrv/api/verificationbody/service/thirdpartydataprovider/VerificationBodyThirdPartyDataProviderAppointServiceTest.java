package uk.gov.pmrv.api.verificationbody.service.thirdpartydataprovider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.thirdpartydataprovider.service.ThirdPartyDataProviderQueryService;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyThirdPartyDataProviderAppointServiceTest {

    @InjectMocks
    private VerificationBodyThirdPartyDataProviderAppointService service;

    @Mock
    private VerificationBodyQueryService verificationBodyQueryService;
    @Mock
    private ThirdPartyDataProviderQueryService thirdPartyDataProviderQueryService;

    @Test
    void appointThirdPartyDataProviderToVerificationBody() {
        Long thirdPartyDataProviderId = 1L;
        Long verificationBodyId = 2L;
        VerificationBody verificationBody = VerificationBody.builder()
            .id(verificationBodyId)
            .status(VerificationBodyStatus.ACTIVE)
            .thirdPartyDataProviderId(3L).build();

        when(verificationBodyQueryService.getVerificationBodyById(verificationBodyId)).thenReturn(verificationBody);
        when(thirdPartyDataProviderQueryService.existsById(thirdPartyDataProviderId)).thenReturn(true);

        service.appointThirdPartyDataProviderToVerificationBody(thirdPartyDataProviderId, verificationBodyId);

        assertThat(verificationBody.getThirdPartyDataProviderId()).isEqualTo(thirdPartyDataProviderId);

        verify(verificationBodyQueryService).getVerificationBodyById(verificationBodyId);
        verify(thirdPartyDataProviderQueryService).existsById(thirdPartyDataProviderId);

        verifyNoMoreInteractions(verificationBodyQueryService, thirdPartyDataProviderQueryService);
    }

    @Test
    void appointThirdPartyDataProviderToVerificationBody_throws_third_party_data_provider_already_appointed() {
        Long thirdPartyDataProviderId = 1L;
        Long verificationBodyId = 2L;
        VerificationBody verificationBody = VerificationBody.builder()
            .id(verificationBodyId)
            .status(VerificationBodyStatus.ACTIVE)
            .thirdPartyDataProviderId(thirdPartyDataProviderId).build();

        when(verificationBodyQueryService.getVerificationBodyById(verificationBodyId)).thenReturn(verificationBody);
        when(thirdPartyDataProviderQueryService.existsById(thirdPartyDataProviderId)).thenReturn(true);

        BusinessException be = assertThrows(BusinessException.class, () ->
            service.appointThirdPartyDataProviderToVerificationBody(thirdPartyDataProviderId, verificationBodyId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.THIRD_PARTY_DATA_PROVIDER_ALREADY_APPOINTED_TO_VERIFICATION_BODY);

        verify(verificationBodyQueryService).getVerificationBodyById(verificationBodyId);
        verify(thirdPartyDataProviderQueryService).existsById(thirdPartyDataProviderId);

        verifyNoMoreInteractions(verificationBodyQueryService, thirdPartyDataProviderQueryService);
    }

    @Test
    void appointThirdPartyDataProviderToVerificationBody_throws_resource_not_found() {
        Long thirdPartyDataProviderId = 1L;
        Long verificationBodyId = 2L;
        VerificationBody verificationBody = VerificationBody.builder()
            .id(verificationBodyId)
            .status(VerificationBodyStatus.ACTIVE)
            .thirdPartyDataProviderId(3L).build();

        when(verificationBodyQueryService.getVerificationBodyById(verificationBodyId)).thenReturn(verificationBody);
        when(thirdPartyDataProviderQueryService.existsById(thirdPartyDataProviderId)).thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class, () ->
            service.appointThirdPartyDataProviderToVerificationBody(thirdPartyDataProviderId, verificationBodyId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(verificationBodyQueryService).getVerificationBodyById(verificationBodyId);
        verify(thirdPartyDataProviderQueryService).existsById(thirdPartyDataProviderId);

        verifyNoMoreInteractions(verificationBodyQueryService, thirdPartyDataProviderQueryService);
    }

    @ParameterizedTest
    @EnumSource(value = VerificationBodyStatus.class, names = {"ACTIVE"}, mode = EnumSource.Mode.EXCLUDE)
    void appointThirdPartyDataProviderToVerificationBody_throws_invalid_status(VerificationBodyStatus status) {
        Long thirdPartyDataProviderId = 1L;
        Long verificationBodyId = 2L;
        VerificationBody verificationBody = VerificationBody.builder()
            .id(verificationBodyId)
            .status(status)
            .thirdPartyDataProviderId(3L).build();

        when(verificationBodyQueryService.getVerificationBodyById(verificationBodyId)).thenReturn(verificationBody);

        BusinessException be = assertThrows(BusinessException.class, () ->
            service.appointThirdPartyDataProviderToVerificationBody(thirdPartyDataProviderId, verificationBodyId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.VERIFICATION_BODY_INVALID_STATUS);

        verify(verificationBodyQueryService).getVerificationBodyById(verificationBodyId);

        verifyNoMoreInteractions(verificationBodyQueryService);
        verifyNoInteractions(thirdPartyDataProviderQueryService);
    }
}