package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.service.PermitIssuanceRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationRequestQueryService;

@Service
@RequiredArgsConstructor
class PermitReissueCreatePermitDocumentService {

	private final PermitQueryService permitQueryService;
	private final PermitVariationRequestQueryService permitVariationRequestQueryService;
	private final PermitIssuanceRequestQueryService permitIssuanceRequestQueryService;
	private final PermitCreateDocumentService permitCreateDocumentService;
	
	@Transactional
    public CompletableFuture<FileInfoDTO> create(Request request) {
        final ReissueRequestMetadata requestMetadata = (ReissueRequestMetadata) request.getMetadata();
        final Long accountId = request.getAccountId();
        final PermitEntityDto permitEntityDto = permitQueryService.getPermitByAccountId(accountId);
        
        final List<PermitVariationRequestInfo> variationRequests = permitVariationRequestQueryService
				.findPermitVariationRequests(accountId);
        
		final PermitIssuanceRequestMetadata issuanceMetadata = permitIssuanceRequestQueryService
				.findPermitIssuanceMetadataByAccountId(accountId);
        
		return permitCreateDocumentService.generateDocumentAsync(request, 
				requestMetadata.getSignatory(),
				permitEntityDto, 
				issuanceMetadata,
				variationRequests);
    }
}
