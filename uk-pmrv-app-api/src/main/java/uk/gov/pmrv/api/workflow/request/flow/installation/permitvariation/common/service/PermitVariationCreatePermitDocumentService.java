package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.service.PermitIssuanceRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.mapper.PermitVariationMapper;

@Service
@RequiredArgsConstructor
public class PermitVariationCreatePermitDocumentService {

	private final RequestService requestService;
    private final PermitQueryService permitQueryService;
    private final DateService dateService;
    private final PermitVariationRequestQueryService permitVariationRequestQueryService;
    private final PermitIssuanceRequestQueryService permitIssuanceRequestQueryService;
    private final PermitCreateDocumentService permitCreateDocumentService;
    private final PermitVariationMapper permitVariationMapper = Mappers.getMapper(PermitVariationMapper.class);
    
    @Transactional
    public CompletableFuture<FileInfoDTO> create(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
        final String signatory = requestPayload.getDecisionNotification().getSignatory();
        final Long accountId = request.getAccountId();
        final PermitEntityDto permitEntityDto = permitQueryService.getPermitByAccountId(accountId);
        
        final PermitVariationRequestInfo variationCurrentRequest = permitVariationMapper.toPermitVariationRequestInfo(request, dateService.getLocalDateTime());
        final List<PermitVariationRequestInfo> variationHistoricalRequests = permitVariationRequestQueryService
				.findPermitVariationRequests(accountId);
        
		final PermitIssuanceRequestMetadata issuanceMetadata = permitIssuanceRequestQueryService
				.findPermitIssuanceMetadataByAccountId(accountId);
        
		return permitCreateDocumentService.generateDocumentAsync(request, 
				signatory,
				permitEntityDto, 
				issuanceMetadata,
				Stream.concat(variationHistoricalRequests.stream(), List.of(variationCurrentRequest).stream())
						.collect(Collectors.toList()));
    }
}
