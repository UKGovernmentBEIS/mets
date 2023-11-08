package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.utils.ConcurrencyUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaApprovedGenerateDocumentsService {

	private final RequestService requestService;
	private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    private final EmissionsMonitoringPlanService emissionsMonitoringPlanService;
	private final EmpVariationCorsiaCreateEmpDocumentService createEmpDocumentService;
	private final EmpVariationCorsiaOfficialNoticeService officialNoticeService;
	
	@Transactional
	public void generateDocuments(String requestId, boolean regulatorLed) {
		CompletableFuture<FileInfoDTO> empDocumentFuture = null;
		CompletableFuture<FileInfoDTO> officialNoticeFuture = null;
		CompletableFuture<Void> allFutures = null;

		try {
			empDocumentFuture = createEmpDocumentService.create(requestId);
			officialNoticeFuture = regulatorLed
					? officialNoticeService.generateApprovedOfficialNoticeRegulatorLed(requestId)
					: officialNoticeService.generateApprovedOfficialNotice(requestId);
			
			allFutures = CompletableFuture.allOf(empDocumentFuture, officialNoticeFuture);
        	allFutures.get();
			
			final FileInfoDTO empDocument = empDocumentFuture.get();
			final FileInfoDTO officialNotice = officialNoticeFuture.get();
			
			final Request request = requestService.findRequestById(requestId);
			final EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
			final EmissionsMonitoringPlanCorsiaDTO empDto = 
				emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(request.getAccountId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
			
			requestPayload.setEmpDocument(empDocument);
            requestPayload.setOfficialNotice(officialNotice);
            emissionsMonitoringPlanService.setFileDocumentUuid(empDto.getId(), empDocument.getUuid());
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
