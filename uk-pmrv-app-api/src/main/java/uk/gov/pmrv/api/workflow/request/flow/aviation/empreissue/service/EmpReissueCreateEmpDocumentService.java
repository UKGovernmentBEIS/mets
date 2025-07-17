package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmissionsMonitoringPlanDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.common.mapper.EmissionTradingSchemeToEntityDocumentTemplateTypeMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;

import java.util.concurrent.CompletableFuture;

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
		
		final EmissionsMonitoringPlanDTO<?> emp = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanDTOByAccountId(accountId, accountEmissionTradingScheme)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

		return empCreateDocumentService.generateDocumentAsync(request,
				requestMetadata.getSignatory(),
                emp,
                schemeToEntityDocumentTemplateTypeMapper.map(accountEmissionTradingScheme));
    }
}
