package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import java.util.concurrent.CompletableFuture;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.common.mapper.EmissionTradingSchemeToEntityDocumentTemplateTypeMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;

@Service
@RequiredArgsConstructor
class EmpReissueCreateEmpDocumentService {
	
	private final AccountQueryService accountQueryService;
	private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	private final EmpCreateDocumentService empCreateDocumentService;
	private final EmissionTradingSchemeToEntityDocumentTemplateTypeMapper schemeToEntityDocumentTemplateTypeMapper = Mappers
			.getMapper(EmissionTradingSchemeToEntityDocumentTemplateTypeMapper.class);

	@Transactional
    public CompletableFuture<FileInfoDTO> create(Request request) {
		final ReissueRequestMetadata requestMetadata = (ReissueRequestMetadata) request.getMetadata();
		final Long accountId = request.getAccountId();
		final EmissionTradingScheme accountEmissionTradingScheme = accountQueryService.getAccountEmissionTradingScheme(accountId);
		
		final EmissionsMonitoringPlanUkEtsDTO emp = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        
		return empCreateDocumentService.generateDocumentAsync(request,
				requestMetadata.getSignatory(),
                emp,
                schemeToEntityDocumentTemplateTypeMapper.map(accountEmissionTradingScheme));
    }
}
