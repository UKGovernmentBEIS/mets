package uk.gov.pmrv.api.emissionsmonitoringplan.common.validation;

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
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionsMonitoringPlanValidatorServiceTest {

    @InjectMocks
    private EmissionsMonitoringPlanValidatorService emissionsMonitoringPlanValidatorService;

    @Mock
    private AviationAccountQueryService accountQueryService;

    @Spy
    private ArrayList<EmpTradingSchemeValidatorService> empTradingSchemeValidatorServices;

    @Mock
    private TestEmpTradingSchemeValidatorService testEmpTradingSchemeValidatorService;

    @BeforeEach
    void setUp() {
        empTradingSchemeValidatorServices.add(testEmpTradingSchemeValidatorService);
    }

    @Test
    void validateEmissionsMonitoringPlan() {
        Long accountId = 1L;
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        AviationAccountInfoDTO aviationAccountInfo = AviationAccountInfoDTO.builder().id(accountId).emissionTradingScheme(emissionTradingScheme).build();
        EmissionsMonitoringPlanContainer empContainer = Mockito.mock(EmissionsMonitoringPlanContainer.class);

        when(accountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(aviationAccountInfo);
        when(testEmpTradingSchemeValidatorService.getEmissionTradingScheme()).thenReturn(emissionTradingScheme);

        emissionsMonitoringPlanValidatorService.validateEmissionsMonitoringPlan(accountId, empContainer);

        verify(accountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
        verify(testEmpTradingSchemeValidatorService, times(1)).getEmissionTradingScheme();
        verify(testEmpTradingSchemeValidatorService, times(1)).validateEmissionsMonitoringPlan(empContainer);
    }

    @Test
    void validateEmissionsMonitoringPlan_no_validator_found() {
        Long accountId = 1L;
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        AviationAccountInfoDTO aviationAccountInfo = AviationAccountInfoDTO.builder().id(accountId).emissionTradingScheme(emissionTradingScheme).build();
        EmissionsMonitoringPlanContainer empContainer = Mockito.mock(EmissionsMonitoringPlanContainer.class);

        when(accountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(aviationAccountInfo);
        when(testEmpTradingSchemeValidatorService.getEmissionTradingScheme()).thenReturn(EmissionTradingScheme.CORSIA);

        BusinessException be = assertThrows(BusinessException.class,
            () -> emissionsMonitoringPlanValidatorService.validateEmissionsMonitoringPlan(accountId, empContainer));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(accountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
        verify(testEmpTradingSchemeValidatorService, times(1)).getEmissionTradingScheme();
        verify(testEmpTradingSchemeValidatorService, never()).validateEmissionsMonitoringPlan(empContainer);
    }

    private static class TestEmpTradingSchemeValidatorService implements EmpTradingSchemeValidatorService<EmissionsMonitoringPlanContainer> {
        @Override
        public void validateEmissionsMonitoringPlan(EmissionsMonitoringPlanContainer empContainer) {

        }

        @Override
        public EmissionTradingScheme getEmissionTradingScheme() {
            return null;
        }
    }
}