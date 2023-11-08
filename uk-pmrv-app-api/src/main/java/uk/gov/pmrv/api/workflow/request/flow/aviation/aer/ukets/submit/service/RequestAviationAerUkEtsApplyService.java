package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsSaveApplicationRequestTaskActionPayload;

@Service
public class RequestAviationAerUkEtsApplyService {

    @Transactional
    public void applySaveAction(AviationAerUkEtsSaveApplicationRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
        AviationAerUkEtsApplicationSubmitRequestTaskPayload taskPayload = (AviationAerUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        taskPayload.setAer(taskActionPayload.getAer());
        taskPayload.setReportingRequired(taskActionPayload.getReportingRequired());
        taskPayload.setReportingObligationDetails(taskActionPayload.getReportingObligationDetails());
        taskPayload.setAerSectionsCompleted(taskActionPayload.getAerSectionsCompleted());

        // Reset verification
        taskPayload.setVerificationPerformed(false);
    }
}
