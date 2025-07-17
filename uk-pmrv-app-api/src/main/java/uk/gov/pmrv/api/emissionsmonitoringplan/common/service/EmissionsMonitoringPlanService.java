package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import lombok.RequiredArgsConstructor;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmissionsMonitoringPlanRepository;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmissionsMonitoringPlanValidatorService;

@Service
@RequiredArgsConstructor
public class EmissionsMonitoringPlanService {

    private final EmissionsMonitoringPlanRepository emissionsMonitoringPlanRepository;
    private final EmissionsMonitoringPlanIdentifierGenerator empIdentifierGenerator;
    private final AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;
    private final EmissionsMonitoringPlanValidatorService empValidatorService;

    @Transactional
    public String submitEmissionsMonitoringPlan(Long accountId, EmissionsMonitoringPlanContainer empContainer) {
        empValidatorService.validateEmissionsMonitoringPlan(accountId, empContainer);

        String empId = empIdentifierGenerator.generate(accountId);

        //submit
        EmissionsMonitoringPlanEntity empEntity = EmissionsMonitoringPlanEntity.builder()
            .id(empId)
            .accountId(accountId)
            .empContainer(empContainer)
            .build();

        emissionsMonitoringPlanRepository.save(empEntity);

        accountSearchAdditionalKeywordService.storeKeywordForAccount(empId, accountId);

        return empEntity.getId();
    }
    
    @Transactional
    public void updateEmissionsMonitoringPlan(Long accountId, EmissionsMonitoringPlanContainer empContainer) {
    	// validate
    	empValidatorService.validateEmissionsMonitoringPlan(accountId, empContainer);

    	EmissionsMonitoringPlanEntity empEntity = emissionsMonitoringPlanRepository.findByAccountId(accountId)
				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        
        // update emp
    	empEntity.setEmpContainer(empContainer);
		doIncrementEmpConsolidationNumber(empEntity);
    }
    
    @Transactional
    public void incrementEmpConsolidationNumber(Long accountId) {
    	EmissionsMonitoringPlanEntity empEntity = emissionsMonitoringPlanRepository.findByAccountId(accountId)
				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    	doIncrementEmpConsolidationNumber(empEntity);
    }
    
    @Transactional
    public void setFileDocumentUuid(final String empId, final String fileDocumentUuid) {
        emissionsMonitoringPlanRepository.updateFileDocumentUuid(empId, fileDocumentUuid);
    }
    
    private void doIncrementEmpConsolidationNumber(EmissionsMonitoringPlanEntity empEntity) {
    	empEntity.setConsolidationNumber(empEntity.getConsolidationNumber() + 1);
	}
}
