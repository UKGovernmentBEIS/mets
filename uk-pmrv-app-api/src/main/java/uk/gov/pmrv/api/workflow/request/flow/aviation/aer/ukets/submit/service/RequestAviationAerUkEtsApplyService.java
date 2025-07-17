package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service.AviationAerUkEtsSubmitRequestTaskSyncAerAttachmentsService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsSaveApplicationRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class RequestAviationAerUkEtsApplyService {
	
	private final AviationAerUkEtsSubmitRequestTaskSyncAerAttachmentsService syncAerAttachmentsService;

    @Transactional
    public void applySaveAction(AviationAerUkEtsSaveApplicationRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
		AviationAerUkEtsApplicationSubmitRequestTaskPayload taskPayload = (AviationAerUkEtsApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		syncAerAttachmentsService.sync(taskActionPayload.getReportingRequired(), taskPayload);
		
		taskPayload.setAer(taskActionPayload.getAer());
        taskPayload.setAerSectionsCompleted(taskActionPayload.getAerSectionsCompleted());

        taskPayload.setReportingRequired(taskActionPayload.getReportingRequired());
        taskPayload.setReportingObligationDetails(taskActionPayload.getReportingObligationDetails());

        // Reset verification
        taskPayload.setVerificationPerformed(false);
    }
}
