package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.service.PermitIssuanceRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;

@ExtendWith(MockitoExtension.class)
public class PermitVariationCreatePermitDocumentServiceTest {

	@InjectMocks
    private PermitVariationCreatePermitDocumentService cut;
	
	@Mock
	private RequestService requestService;

    @Mock
    private PermitVariationRequestQueryService permitVariationRequestQueryService;

    @Mock
    private PermitIssuanceRequestQueryService permitIssuanceRequestQueryService;
    
    @Mock
    private PermitQueryService permitQueryService;
    
    @Mock
    private PermitCreateDocumentService permitCreateDocumentService;
    
    @Mock
    private DateService dateService;
    
    @Test
    void create() throws InterruptedException, ExecutionException {
        final String requestId = "1";
        final Long accountId = 5L;
        final String signatory = "signatory";
        
        final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
            .decisionNotification(DecisionNotification.builder()
                .signatory(signatory)
                .build())
            .build();
        final PermitVariationRequestMetadata variationCurrentMetadata = PermitVariationRequestMetadata.builder()
        		.logChanges("logChanges")
        		.build();
        final Request request = Request.builder()
        		.type(RequestType.PERMIT_VARIATION)
        		.accountId(accountId)
        		.payload(requestPayload)
        		.metadata(variationCurrentMetadata)
        		.build();
        
        final PermitEntityDto permitEntityDto = PermitEntityDto.builder().id("permitId").build();
        final FileInfoDTO permitDocument = FileInfoDTO.builder().uuid("uuid").build();
        final List<PermitVariationRequestInfo> variationHistoricalRequestInfo = 
        		List.of(
        				PermitVariationRequestInfo.builder()
        				.metadata(PermitVariationRequestMetadata.builder().logChanges("logChanges_history1").build())
        				.build(),
        				PermitVariationRequestInfo.builder()
        				.metadata(PermitVariationRequestMetadata.builder().logChanges("logChanges_history2").build())
        				.build()
        				);
        final PermitIssuanceRequestMetadata issuanceMetadata = PermitIssuanceRequestMetadata.builder()
        		.type(RequestMetadataType.PERMIT_ISSUANCE)
        		.build();

        final LocalDateTime currentVariationRequestEndDate = LocalDateTime.now();
        final PermitVariationRequestInfo variationCurrentRequestInfo = PermitVariationRequestInfo.builder()
        		.metadata(variationCurrentMetadata)
        		.endDate(currentVariationRequestEndDate)
        		.build();
        
        final List<PermitVariationRequestInfo> allVariationRequestInfo = 
        		Stream.concat(variationHistoricalRequestInfo.stream(), List.of(variationCurrentRequestInfo).stream())
		.collect(Collectors.toList());
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(permitQueryService.getPermitByAccountId(accountId)).thenReturn(permitEntityDto);
        when(dateService.getLocalDateTime()).thenReturn(currentVariationRequestEndDate);
        when(permitVariationRequestQueryService.findPermitVariationRequests(accountId))
        	.thenReturn(variationHistoricalRequestInfo);
        when(permitIssuanceRequestQueryService.findPermitIssuanceMetadataByAccountId(accountId))
    		.thenReturn(issuanceMetadata);

        when(permitCreateDocumentService.generateDocumentAsync(request, signatory, permitEntityDto, issuanceMetadata, allVariationRequestInfo))
            .thenReturn(CompletableFuture.completedFuture(permitDocument));

        CompletableFuture<FileInfoDTO> result = cut.create(requestId);
        
        assertThat(result.get()).isEqualTo(permitDocument);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(permitQueryService, times(1)).getPermitByAccountId(accountId);
        verify(dateService, times(1)).getLocalDateTime();
        verify(permitVariationRequestQueryService, times(1)).findPermitVariationRequests(accountId);
        verify(permitIssuanceRequestQueryService, times(1)).findPermitIssuanceMetadataByAccountId(accountId);
        verify(permitCreateDocumentService, times(1)).generateDocumentAsync(request, signatory, permitEntityDto, issuanceMetadata, allVariationRequestInfo);
    }
    
}
