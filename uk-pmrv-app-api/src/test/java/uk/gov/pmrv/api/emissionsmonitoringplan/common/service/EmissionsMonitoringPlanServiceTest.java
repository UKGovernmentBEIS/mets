package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmissionsMonitoringPlanRepository;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmissionsMonitoringPlanValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmissionsMonitoringPlanServiceTest {

    @InjectMocks
    private EmissionsMonitoringPlanService emissionsMonitoringPlanService;

    @Mock
    private EmissionsMonitoringPlanRepository emissionsMonitoringPlanRepository;

    @Mock
    private EmissionsMonitoringPlanIdentifierGenerator empIdentifierGenerator;

    @Mock
    private AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;

    @Mock
    private EmissionsMonitoringPlanValidatorService emissionsMonitoringPlanValidatorService;

    @Test
    void submitEmissionsMonitoringPlan() {
        Long accountId = 1L;
        String empId = "empId";
        EmissionsMonitoringPlanEntity empEntity =
                EmissionsMonitoringPlanEntity.builder().id(empId).accountId(accountId).build();
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder().build();

        when(empIdentifierGenerator.generate(accountId)).thenReturn(empId);

        emissionsMonitoringPlanService.submitEmissionsMonitoringPlan(accountId, empContainer);

        verify(emissionsMonitoringPlanValidatorService, times(1)).validateEmissionsMonitoringPlan(accountId, empContainer);
        verify(empIdentifierGenerator, times(1)).generate(accountId);
        verify(emissionsMonitoringPlanRepository, times(1)).save(empEntity);
        verify(accountSearchAdditionalKeywordService, times(1)).storeKeywordForAccount(empId, accountId);
    }
    
    @Test
    void updateEmissionsMonitoringPlan() {
        Long accountId = 1L;
        String empId = "empId";
        EmissionsMonitoringPlanEntity empEntity =
                EmissionsMonitoringPlanEntity.builder().id(empId).accountId(accountId).build();
        empEntity.setConsolidationNumber(1);
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder().build();

        when(emissionsMonitoringPlanRepository.findByAccountId(accountId)).thenReturn(Optional.of(empEntity));

        emissionsMonitoringPlanService.updateEmissionsMonitoringPlan(accountId, empContainer);

        assertThat(empEntity.getConsolidationNumber()).isEqualTo(2);
        assertThat(empEntity.getEmpContainer()).isEqualTo(empContainer);
        verify(emissionsMonitoringPlanValidatorService, times(1)).validateEmissionsMonitoringPlan(accountId, empContainer);
        verify(emissionsMonitoringPlanRepository, times(1)).findByAccountId(accountId);
    }
    
    @Test
    void updateEmissionsMonitoringPlan_emp_not_found() {
        Long accountId = 1L;
        String empId = "empId";
        EmissionsMonitoringPlanEntity empEntity =
                EmissionsMonitoringPlanEntity.builder().id(empId).accountId(accountId).build();
        empEntity.setConsolidationNumber(1);
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder().build();

        when(emissionsMonitoringPlanRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class,
                () -> emissionsMonitoringPlanService.updateEmissionsMonitoringPlan(accountId, empContainer));
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, be.getErrorCode());
    }
    
    @Test
    void incrementEmpConsolidationNumber() {
    	Long accountId = 1L;
    	String empId = "empId";
        EmissionsMonitoringPlanEntity empEntity =
                EmissionsMonitoringPlanEntity.builder().id(empId).accountId(accountId).build();
        empEntity.setConsolidationNumber(1);
        when(emissionsMonitoringPlanRepository.findByAccountId(accountId)).thenReturn(Optional.of(empEntity));
        
        emissionsMonitoringPlanService.incrementEmpConsolidationNumber(accountId);
        
        assertThat(empEntity.getConsolidationNumber()).isEqualTo(2);
        verify(emissionsMonitoringPlanRepository, times(1)).findByAccountId(accountId);
    }
}