package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.utils.ConcurrencyUtils;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
@Service
@RequiredArgsConstructor
public class PermitIssuanceGrantedGenerateDocumentsService {

	private final RequestService requestService;
	private final PermitService permitService;
	private final PermitQueryService permitQueryService;
	private final PermitIssuanceCreatePermitDocumentService permitIssuanceCreatePermitDocumentService;
	private final PermitIssuanceOfficialNoticeService permitIssuanceOfficialNoticeService;
	
	@Transactional
	public void generateDocuments(String requestId) {
		CompletableFuture<FileInfoDTO> permitDocumentFuture = null;
		CompletableFuture<FileInfoDTO> officialNoticeFuture = null;
		CompletableFuture<Void> allFutures = null;
		
		try {
			permitDocumentFuture = permitIssuanceCreatePermitDocumentService.create(requestId);
			officialNoticeFuture = permitIssuanceOfficialNoticeService.generateGrantedOfficialNotice(requestId);
			
			allFutures = CompletableFuture.allOf(permitDocumentFuture, officialNoticeFuture);
        	allFutures.get();
			
			final FileInfoDTO permitDocument = permitDocumentFuture.get();
			final FileInfoDTO officialNotice = officialNoticeFuture.get();
			
			final Request request = requestService.findRequestById(requestId);
	        final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();
	        final PermitEntityDto permitEntityDto = permitQueryService.getPermitByAccountId(request.getAccountId());

			requestPayload.setPermitDocument(permitDocument);
			requestPayload.setOfficialNotice(officialNotice);
			permitService.setFileDocumentUuid(permitEntityDto.getId(), permitDocument.getUuid());
		} catch (ExecutionException e) {
			Throwable caused = e.getCause();
			if(caused.getClass() == BusinessException.class) {
				throw (BusinessException)caused;
			} else {
				log.error(caused.getMessage());
				throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
			}
		} catch (InterruptedException e) {
			Throwable caused = e.getCause();
			log.error(e.getMessage());
			Thread.currentThread().interrupt();
			throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
		} catch (Exception e) {
			Throwable caused = e.getCause();
			log.error(e.getMessage());
			throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
		} finally {
			ConcurrencyUtils.completeCompletableFutures(permitDocumentFuture, officialNoticeFuture, allFutures);
		}
	}
}
