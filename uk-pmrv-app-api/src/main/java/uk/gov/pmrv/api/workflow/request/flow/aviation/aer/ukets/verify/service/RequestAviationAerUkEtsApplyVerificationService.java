package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload;

@Service
public class RequestAviationAerUkEtsApplyVerificationService {

    @Transactional
    public void applySaveAction(AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload taskActionPayload,
                                RequestTask requestTask) {

        Request request = requestTask.getRequest();

        AviationAerUkEtsRequestPayload aerRequestPayload = ((AviationAerUkEtsRequestPayload) request.getPayload());

        AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload taskPayload =
            (AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.getVerificationReport().setVerificationData(taskActionPayload.getVerificationData());
        taskPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());
        
        
        aerRequestPayload.setVerificationReport(taskPayload.getVerificationReport());
        aerRequestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        aerRequestPayload
                .setVerificationSectionsCompleted(
                        taskActionPayload.getVerificationSectionsCompleted());
    }
}
