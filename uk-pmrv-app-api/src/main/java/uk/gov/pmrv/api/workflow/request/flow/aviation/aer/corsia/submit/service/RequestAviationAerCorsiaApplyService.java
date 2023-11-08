package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaSaveApplicationRequestTaskActionPayload;

@Service
public class RequestAviationAerCorsiaApplyService {

    @Transactional
    public void applySaveAction(final AviationAerCorsiaSaveApplicationRequestTaskActionPayload taskActionPayload, 
                                final RequestTask requestTask) {

        final AviationAerCorsiaApplicationSubmitRequestTaskPayload taskPayload = (AviationAerCorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        taskPayload.setAer(taskActionPayload.getAer());
        taskPayload.setReportingRequired(taskActionPayload.getReportingRequired());
        taskPayload.setReportingObligationDetails(taskActionPayload.getReportingObligationDetails());
        taskPayload.setAerSectionsCompleted(taskActionPayload.getAerSectionsCompleted());

        // Reset verification
        taskPayload.setVerificationPerformed(false);
    }
}
