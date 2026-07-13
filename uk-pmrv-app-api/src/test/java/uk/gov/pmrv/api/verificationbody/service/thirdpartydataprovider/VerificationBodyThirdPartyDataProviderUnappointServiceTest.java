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
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyThirdPartyDataProviderUnappointServiceTest {

    @InjectMocks
    private VerificationBodyThirdPartyDataProviderUnappointService service;

    @Mock
    private VerificationBodyQueryService verificationBodyQueryService;

    @Test
    void unappointThirdPartyDataProviderFromVerificationBody() {
        Long verificationBodyId = 1L;
        VerificationBody verificationBody = VerificationBody.builder()
            .status(VerificationBodyStatus.ACTIVE)
            .id(verificationBodyId)
            .thirdPartyDataProviderId(2L)
            .build();

        when(verificationBodyQueryService.getVerificationBodyById(verificationBodyId)).thenReturn(verificationBody);

        service.unappointThirdPartyDataProviderFromVerificationBody(verificationBodyId);

        assertThat(verificationBody.getThirdPartyDataProviderId()).isNull();

        verify(verificationBodyQueryService).getVerificationBodyById(verificationBodyId);
        verifyNoMoreInteractions(verificationBodyQueryService);
    }


    @ParameterizedTest
    @EnumSource(value = VerificationBodyStatus.class, names = {"ACTIVE"}, mode = EnumSource.Mode.EXCLUDE)
    void unappointThirdPartyDataProviderFromVerificationBody_throws_resource_not_found(VerificationBodyStatus status) {
        Long verificationBodyId = 1L;
        Long thirdPartyDataProviderId = 2L;
        VerificationBody verificationBody = VerificationBody.builder()
            .status(status)
            .id(verificationBodyId)
            .thirdPartyDataProviderId(thirdPartyDataProviderId)
            .build();

        when(verificationBodyQueryService.getVerificationBodyById(verificationBodyId)).thenReturn(verificationBody);

        BusinessException be = assertThrows(BusinessException.class, () ->
            service.unappointThirdPartyDataProviderFromVerificationBody(verificationBodyId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.VERIFICATION_BODY_INVALID_STATUS);

        assertThat(verificationBody.getThirdPartyDataProviderId()).isEqualTo(thirdPartyDataProviderId);

        verify(verificationBodyQueryService).getVerificationBodyById(verificationBodyId);
        verifyNoMoreInteractions(verificationBodyQueryService);
    }
}