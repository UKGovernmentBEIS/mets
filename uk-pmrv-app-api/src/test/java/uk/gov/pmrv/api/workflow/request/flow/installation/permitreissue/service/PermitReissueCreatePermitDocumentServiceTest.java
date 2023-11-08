package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.service.PermitIssuanceRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationRequestQueryService;

@ExtendWith(MockitoExtension.class)
public class PermitReissueCreatePermitDocumentServiceTest {

	@InjectMocks
	private PermitReissueCreatePermitDocumentService cut;

	@Mock
	private PermitQueryService permitQueryService;
	
	@Mock
	private PermitVariationRequestQueryService permitVariationRequestQueryService;
	
	@Mock
	private PermitIssuanceRequestQueryService permitIssuanceRequestQueryService;
	
	@Mock
	private PermitCreateDocumentService permitCreateDocumentService;
	
	@Test
	void create() throws InterruptedException, ExecutionException {
		Long accountId = 1L;
		
		ReissueRequestMetadata metadata = ReissueRequestMetadata.builder()
    			.signatory("signatory")
    			.build();
		
		Request request = Request.builder()
				.accountId(accountId)
				.metadata(metadata)
				.build();
		
		PermitEntityDto permitEntityDto = PermitEntityDto.builder()
				.accountId(accountId)
				.permitContainer(PermitContainer.builder()
						.permit(Permit.builder().abbreviations(Abbreviations.builder().exist(true).build()).build())
						.build())
				.build();
		
		final List<PermitVariationRequestInfo> variationRequests = 
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
		
		final FileInfoDTO permitDocument = FileInfoDTO.builder().uuid("uuid").build();
		
		when(permitQueryService.getPermitByAccountId(accountId)).thenReturn(permitEntityDto);
		when(permitVariationRequestQueryService.findPermitVariationRequests(accountId)).thenReturn(variationRequests);
		when(permitIssuanceRequestQueryService.findPermitIssuanceMetadataByAccountId(accountId)).thenReturn(issuanceMetadata);
		when(permitCreateDocumentService.generateDocumentAsync(request, "signatory", permitEntityDto, issuanceMetadata, variationRequests))
        .thenReturn(CompletableFuture.completedFuture(permitDocument));
		
		CompletableFuture<FileInfoDTO> result = cut.create(request);
		assertThat(result.get()).isEqualTo(permitDocument);
		
		verify(permitQueryService, times(1)).getPermitByAccountId(accountId);
		verify(permitVariationRequestQueryService, times(1)).findPermitVariationRequests(accountId);
        verify(permitIssuanceRequestQueryService, times(1)).findPermitIssuanceMetadataByAccountId(accountId);
        verify(permitCreateDocumentService, times(1)).generateDocumentAsync(request, "signatory", permitEntityDto, issuanceMetadata, variationRequests);
		
	}
}
