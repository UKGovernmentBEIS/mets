package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class PermitReissueGenerateDocumentsServiceTest {

	@InjectMocks
	private PermitReissueGenerateDocumentsService cut;

	@Mock
	private PermitReissueCreatePermitDocumentService permitReissueCreatePermitDocumentService;
	
	@Mock
	private ReissueOfficialNoticeService reissueOfficialNoticeService;
	
	@Mock
	private PermitQueryService permitQueryService;
	
	@Mock
	private PermitService permitService;
	
	@Test
	void generateDocuments() {
        final Long accountId = 1L;
        
        final ReissueRequestPayload requestPayload = ReissueRequestPayload.builder()
            .build();
        
    	final Request request = Request.builder()
        		.type(RequestType.PERMIT_REISSUE)
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
    	
    	when(permitReissueCreatePermitDocumentService.create(request))
    		.thenReturn(CompletableFuture.completedFuture(permitDocument));
    	when(reissueOfficialNoticeService.generateOfficialNotice(request))
    		.thenReturn(CompletableFuture.completedFuture(officialNotice));
    	when(permitQueryService.getPermitByAccountId(accountId)).thenReturn(permitEntityDto);
    	
    	cut.generateDocuments(request);
    	
    	verify(permitReissueCreatePermitDocumentService, times(1)).create(request);
    	verify(reissueOfficialNoticeService, times(1)).generateOfficialNotice(request);
    	verify(permitQueryService, times(1)).getPermitByAccountId(accountId);
    	verify(permitService, times(1)).setFileDocumentUuid(permitEntityDto.getId(), permitPdfUuid.toString());
    	
    	assertThat(requestPayload.getDocument()).isEqualTo(permitDocument);
    	assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
	}
}
