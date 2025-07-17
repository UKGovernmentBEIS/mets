package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service.AviationAerCorsiaSubmitRequestTaskSyncAerAttachmentsService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaSaveApplicationRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class RequestAviationAerCorsiaApplyService {
	
	private final AviationAerCorsiaSubmitRequestTaskSyncAerAttachmentsService syncAerAttachmentsService;

    @Transactional
    public void applySaveAction(final AviationAerCorsiaSaveApplicationRequestTaskActionPayload taskActionPayload, 
                                final RequestTask requestTask) {
        final AviationAerCorsiaApplicationSubmitRequestTaskPayload taskPayload = (AviationAerCorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        syncAerAttachmentsService.sync(taskActionPayload.getReportingRequired(), taskPayload);
        
        taskPayload.setAer(taskActionPayload.getAer());
        taskPayload.setAerSectionsCompleted(taskActionPayload.getAerSectionsCompleted());
        
        taskPayload.setReportingRequired(taskActionPayload.getReportingRequired());
        taskPayload.setReportingObligationDetails(taskActionPayload.getReportingObligationDetails());

        // Reset verification
        taskPayload.setVerificationPerformed(false);
    }
}
