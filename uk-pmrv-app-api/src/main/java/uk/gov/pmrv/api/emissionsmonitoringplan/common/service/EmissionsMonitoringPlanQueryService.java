package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.EmpAuthorityInfoProvider;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmissionsMonitoringPlanDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpAccountDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmissionsMonitoringPlanRepository;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.transform.EmissionsMonitoringPlanMapper;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmissionsMonitoringPlanQueryService implements EmpAuthorityInfoProvider {

    private final EmissionsMonitoringPlanRepository emissionsMonitoringPlanRepository;
    private final FileDocumentService fileDocumentService;

    private static final EmissionsMonitoringPlanMapper EMISSIONS_MONITORING_PLAN_MAPPER = Mappers.getMapper(EmissionsMonitoringPlanMapper.class);

    @Transactional(readOnly = true)
    public Optional<EmpDetailsDTO> getEmissionsMonitoringPlanDetailsDTOByAccountId(Long accountId) {
        return emissionsMonitoringPlanRepository.findByAccountId(accountId).map(empEntity -> EMISSIONS_MONITORING_PLAN_MAPPER.toEmpDetailsDTO(empEntity,
                        Optional.ofNullable(empEntity.getFileDocumentUuid()).map((fileDocumentService::getFileInfoDTO)).orElse(null)));
    }

    public Optional<String> getEmpIdByAccountId(Long accountId) {
        return emissionsMonitoringPlanRepository.findEmpIdByAccountId(accountId);
    }
    
    public Map<Long, EmpAccountDTO> getEmpAccountsByAccountIds(Set<Long> accountIds) {
		return emissionsMonitoringPlanRepository.findAllByAccountIdIn(accountIds).stream()
				.collect(Collectors.toMap(EmpAccountDTO::getAccountId, Function.identity()));
    }

    @Transactional(readOnly = true)
    public Optional<EmissionsMonitoringPlanUkEtsDTO> getEmissionsMonitoringPlanUkEtsDTOByAccountId(Long accountId) {
        return emissionsMonitoringPlanRepository.findByAccountId(accountId).map(EMISSIONS_MONITORING_PLAN_MAPPER::toEmissionsMonitoringPlanUkEtsDTO);
    }

    @Transactional(readOnly = true)
    public Optional<EmissionsMonitoringPlanCorsiaDTO> getEmissionsMonitoringPlanCorsiaDTOByAccountId(Long accountId) {
        return emissionsMonitoringPlanRepository.findByAccountId(accountId).map(EMISSIONS_MONITORING_PLAN_MAPPER::toEmissionsMonitoringPlanCorsiaDTO);
    }

    @Transactional(readOnly = true)
    public Optional<EmissionsMonitoringPlanDTO> getEmissionsMonitoringPlanDTOByAccountId(Long accountId, EmissionTradingScheme accountEmissionTradingScheme) {
        return emissionsMonitoringPlanRepository.findByAccountId(accountId)
                .map(emp -> EMISSIONS_MONITORING_PLAN_MAPPER.toEmissionsMonitoringPlanDTO(emp, accountEmissionTradingScheme));
    }

    public EmissionsMonitoringPlanContainer getEmpContainerById(String id) {
        return emissionsMonitoringPlanRepository.findById(id)
                .map(EmissionsMonitoringPlanEntity::getEmpContainer)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public EmissionsMonitoringPlanContainer getEmpContainerByIdAndFileDocumentUuid(final String empId, final String fileDocumentUuid) {
        return emissionsMonitoringPlanRepository.findEmpByIdAndFileDocumentUuid(empId, fileDocumentUuid)
                .map(EmissionsMonitoringPlanEntity::getEmpContainer)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public Long getEmpAccountById(String id) {
        return emissionsMonitoringPlanRepository.findEmpAccountById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public int getEmissionsMonitoringPlanConsolidationNumberByAccountId(Long accountId) {
    	return emissionsMonitoringPlanRepository.findByAccountId(accountId)
    			.map(EmissionsMonitoringPlanEntity::getConsolidationNumber)
    			.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
