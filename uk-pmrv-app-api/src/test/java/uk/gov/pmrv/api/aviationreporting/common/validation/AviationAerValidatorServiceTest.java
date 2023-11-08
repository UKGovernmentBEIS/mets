package uk.gov.pmrv.api.aviationreporting.common.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerContainer;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerValidatorServiceTest {

    @InjectMocks
    private AviationAerValidatorService aerValidatorService;

    @Mock
    private AviationAccountQueryService accountQueryService;

    @Spy
    private ArrayList<AviationAerTradingSchemeValidatorService> aerTradingSchemeValidatorServices;

    @Mock
    private AviationAerValidatorServiceTest.TestAviationAerTradingSchemeValidatorService testAerTradingSchemeValidatorService;

    @BeforeEach
    void setUp() {
        aerTradingSchemeValidatorServices.add(testAerTradingSchemeValidatorService);
    }

    @Test
    void validateEmissionsMonitoringPlan() {
        Long accountId = 1L;
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        AviationAccountInfoDTO aviationAccountInfo = AviationAccountInfoDTO.builder().id(accountId).emissionTradingScheme(emissionTradingScheme).build();
        AviationAerContainer aerContainer = Mockito.mock(AviationAerContainer.class);

        when(accountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(aviationAccountInfo);
        when(testAerTradingSchemeValidatorService.getEmissionTradingScheme()).thenReturn(emissionTradingScheme);

        aerValidatorService.validate(accountId, aerContainer);

        verify(accountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
        verify(testAerTradingSchemeValidatorService, times(1)).getEmissionTradingScheme();
        verify(testAerTradingSchemeValidatorService, times(1)).validate(aerContainer);
    }

    @Test
    void validateEmissionsMonitoringPlan_no_validator_found() {
        Long accountId = 1L;
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        AviationAccountInfoDTO aviationAccountInfo = AviationAccountInfoDTO.builder().id(accountId).emissionTradingScheme(emissionTradingScheme).build();
        AviationAerContainer aerContainer = Mockito.mock(AviationAerContainer.class);

        when(accountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(aviationAccountInfo);
        when(testAerTradingSchemeValidatorService.getEmissionTradingScheme()).thenReturn(EmissionTradingScheme.CORSIA);

        BusinessException be = assertThrows(BusinessException.class, () -> aerValidatorService.validate(accountId, aerContainer));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(accountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
        verify(testAerTradingSchemeValidatorService, times(1)).getEmissionTradingScheme();
        verify(testAerTradingSchemeValidatorService, never()).validate(aerContainer);
    }

    private static class TestAviationAerTradingSchemeValidatorService implements AviationAerTradingSchemeValidatorService<AviationAerContainer> {

        @Override
        public void validate(AviationAerContainer aerContainer) {

        }

        @Override
        public void validateAer(AviationAerContainer aerContainer) {

        }

        @Override
        public EmissionTradingScheme getEmissionTradingScheme() {
            return null;
        }
    }
}