package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationGrantedGenerateDocumentsServiceTest {

	@InjectMocks
    private PermitVariationGrantedGenerateDocumentsService cut;
	
	@Mock
	private RequestService requestService;

    @Mock
    private PermitService permitService;
    
    @Mock
    private PermitQueryService permitQueryService;
    
    @Mock
    private PermitVariationCreatePermitDocumentService permitVariationCreatePermitDocumentService;
    
    @Mock
    private PermitVariationOfficialNoticeService permitVariationOfficialNoticeService;
    
    @Test
    void generateDocuments() {
    	final String requestId = "1";
        final Long accountId = 5L;
        final String signatory = "signatory";
        
        final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
            .decisionNotification(DecisionNotification.builder()
                .signatory(signatory)
                .build())
            .build();
        
    	final Request request = Request.builder()
        		.type(RequestType.PERMIT_VARIATION)
        		.accountId(accountId)
        		.payload(requestPayload)
        		.build();
    	
    	UUID permitPdfUuid = UUID.randomUUID();
    	FileInfoDTO permitDocument = FileInfoDTO.builder()
    			.name("permit.pdf")
    			.uuid(permitPdfUuid.toString())
    			.build();
    	
    	UUID officialNoticePdfUuid = UUID.randomUUID();
    	FileInfoDTO officialNotice = FileInfoDTO.builder()
    			.name("offnotice.pdf")
    			.uuid(officialNoticePdfUuid.toString())
    			.build();
    	
    	final PermitEntityDto permitEntityDto = PermitEntityDto.builder().id("permitId").build();
    	
    	when(permitVariationCreatePermitDocumentService.create(requestId))
    		.thenReturn(CompletableFuture.completedFuture(permitDocument));
    	when(permitVariationOfficialNoticeService.generateGrantedOfficialNotice(requestId))
    		.thenReturn(CompletableFuture.completedFuture(officialNotice));
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	when(permitQueryService.getPermitByAccountId(accountId)).thenReturn(permitEntityDto);
    	
    	cut.generateDocuments(requestId, false);
    	
    	verify(permitVariationCreatePermitDocumentService, times(1)).create(requestId);
    	verify(permitVariationOfficialNoticeService, times(1)).generateGrantedOfficialNotice(requestId);
    	verify(requestService, times(1)).findRequestById(requestId);
    	verify(permitQueryService, times(1)).getPermitByAccountId(accountId);
    	verify(permitService, times(1)).setFileDocumentUuid(permitEntityDto.getId(), permitPdfUuid.toString());
    	
    	assertThat(requestPayload.getPermitDocument()).isEqualTo(permitDocument);
    	assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
    }
    
    @Test
    void generateDocuments_regulator_led() {
    	final String requestId = "1";
        final Long accountId = 5L;
        final String signatory = "signatory";
        
        final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
            .decisionNotification(DecisionNotification.builder()
                .signatory(signatory)
                .build())
            .build();
        
    	final Request request = Request.builder()
        		.type(RequestType.PERMIT_VARIATION)
        		.accountId(accountId)
        		.payload(requestPayload)
        		.build();
    	
    	UUID permitPdfUuid = UUID.randomUUID();
    	FileInfoDTO permitDocument = FileInfoDTO.builder()
    			.name("permit.pdf")
    			.uuid(permitPdfUuid.toString())
    			.build();
    	
    	UUID officialNoticePdfUuid = UUID.randomUUID();
    	FileInfoDTO officialNotice = FileInfoDTO.builder()
    			.name("offnotice.pdf")
    			.uuid(officialNoticePdfUuid.toString())
    			.build();
    	
    	final PermitEntityDto permitEntityDto = PermitEntityDto.builder().id("permitId").build();
    	
    	when(permitVariationCreatePermitDocumentService.create(requestId))
    		.thenReturn(CompletableFuture.completedFuture(permitDocument));
    	when(permitVariationOfficialNoticeService.generateRegulatorLedApprovedOfficialNotice(requestId))
    		.thenReturn(CompletableFuture.completedFuture(officialNotice));
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	when(permitQueryService.getPermitByAccountId(accountId)).thenReturn(permitEntityDto);
    	
    	cut.generateDocuments(requestId, true);
    	
    	verify(permitVariationCreatePermitDocumentService, times(1)).create(requestId);
    	verify(permitVariationOfficialNoticeService, times(1)).generateRegulatorLedApprovedOfficialNotice(requestId);
    	verify(requestService, times(1)).findRequestById(requestId);
    	verify(permitQueryService, times(1)).getPermitByAccountId(accountId);
    	verify(permitService, times(1)).setFileDocumentUuid(permitEntityDto.getId(), permitPdfUuid.toString());
    	
    	assertThat(requestPayload.getPermitDocument()).isEqualTo(permitDocument);
    	assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
    }
    
    @Test
    void generateDocuments_throws_business_exception() {
    	final String requestId = "1";
        final String signatory = "signatory";
        
        final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
            .decisionNotification(DecisionNotification.builder()
                .signatory(signatory)
                .build())
            .build();
        
    	UUID officialNoticePdfUuid = UUID.randomUUID();
    	FileInfoDTO officialNotice = FileInfoDTO.builder()
    			.name("offnotice.pdf")
    			.uuid(officialNoticePdfUuid.toString())
    			.build();
    	
    	when(permitVariationCreatePermitDocumentService.create(requestId)).thenAnswer(answer -> {
    	     CompletableFuture<?> future = new CompletableFuture<>();
    	     future.completeExceptionally(new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, "permit.pdf"));
    	     return future;
    	});
    	
    	when(permitVariationOfficialNoticeService.generateGrantedOfficialNotice(requestId))
    		.thenReturn(CompletableFuture.completedFuture(officialNotice));
    	
    	BusinessException be = assertThrows(BusinessException.class, () -> cut.generateDocuments(requestId, false));
    	assertThat(be.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR);
    	
    	verify(permitVariationCreatePermitDocumentService, times(1)).create(requestId);
    	verify(permitVariationOfficialNoticeService, times(1)).generateGrantedOfficialNotice(requestId);
    	verifyNoInteractions(requestService, permitQueryService, permitService);
    	
    	assertThat(requestPayload.getPermitDocument()).isNull();
    	assertThat(requestPayload.getOfficialNotice()).isNull();
    }
    
    @Test
    void generateDocuments_throws_internal_server_error_exception() {
    	final String requestId = "1";
        final String signatory = "signatory";
        
        final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
            .decisionNotification(DecisionNotification.builder()
                .signatory(signatory)
                .build())
            .build();
        
    	UUID officialNoticePdfUuid = UUID.randomUUID();
    	FileInfoDTO officialNotice = FileInfoDTO.builder()
    			.name("offnotice.pdf")
    			.uuid(officialNoticePdfUuid.toString())
    			.build();
    	
    	when(permitVariationCreatePermitDocumentService.create(requestId)).thenAnswer(answer -> {
    	     CompletableFuture<?> future = new CompletableFuture<>();
    	     future.completeExceptionally(new RuntimeException("something unexpected happened"));
    	     return future;
    	});
    	
    	when(permitVariationOfficialNoticeService.generateGrantedOfficialNotice(requestId))
    		.thenReturn(CompletableFuture.completedFuture(officialNotice));
    	
    	BusinessException be = assertThrows(BusinessException.class, () -> cut.generateDocuments(requestId, false));
    	assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);
    	
    	verify(permitVariationCreatePermitDocumentService, times(1)).create(requestId);
    	verify(permitVariationOfficialNoticeService, times(1)).generateGrantedOfficialNotice(requestId);
    	verifyNoInteractions(requestService, permitQueryService, permitService);
    	
    	assertThat(requestPayload.getPermitDocument()).isNull();
    	assertThat(requestPayload.getOfficialNotice()).isNull();
    }
}
