package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload;

@Service
public class RequestAviationAerCorsiaApplyVerificationService {

    @Transactional
    public void applySaveAction(AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload taskActionPayload,
                                RequestTask requestTask) {
        AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload taskPayload =
                (AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.getVerificationReport().setVerificationData(taskActionPayload.getVerificationData());
        taskPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());
    }
}
