package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.utils.ConcurrencyUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueOfficialNoticeService;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmpReissueGenerateDocumentsService {

	private final EmpReissueCreateEmpDocumentService empReissueCreateEmpDocumentService;
	private final ReissueOfficialNoticeService reissueOfficialNoticeService;
	private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	private final EmissionsMonitoringPlanService emissionsMonitoringPlanService;
	
	@Transactional
	public void generateDocuments(Request request) {
		CompletableFuture<FileInfoDTO> empDocumentFuture = null;
    	CompletableFuture<FileInfoDTO> officialNoticeFuture = null;
    	CompletableFuture<Void> allFutures = null;
    	
		try {
			empDocumentFuture = empReissueCreateEmpDocumentService.create(request);
			officialNoticeFuture = reissueOfficialNoticeService.generateOfficialNotice(request);

			allFutures = CompletableFuture.allOf(empDocumentFuture, officialNoticeFuture);
        	allFutures.get();
			
			final FileInfoDTO empDocument = empDocumentFuture.get();
			final FileInfoDTO officialNotice = officialNoticeFuture.get();
			
			final EmpDetailsDTO empDetailsDTO = emissionsMonitoringPlanQueryService
					.getEmissionsMonitoringPlanDetailsDTOByAccountId(request.getAccountId())
					.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

	        final ReissueRequestPayload requestPayload = (ReissueRequestPayload) request.getPayload();
			requestPayload.setDocument(empDocument);
			requestPayload.setOfficialNotice(officialNotice);
			emissionsMonitoringPlanService.setFileDocumentUuid(empDetailsDTO.getId(), empDocument.getUuid());
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
			ConcurrencyUtils.completeCompletableFutures(empDocumentFuture, officialNoticeFuture, allFutures);
		}
	}
}
