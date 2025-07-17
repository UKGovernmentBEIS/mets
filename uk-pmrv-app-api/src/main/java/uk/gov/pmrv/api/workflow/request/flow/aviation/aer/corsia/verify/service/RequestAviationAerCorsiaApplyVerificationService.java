package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload;

@Service
public class RequestAviationAerCorsiaApplyVerificationService {

    @Transactional
    public void applySaveAction(AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload taskActionPayload,
                                RequestTask requestTask) {

        Request request = requestTask.getRequest();

        AviationAerCorsiaRequestPayload aerRequestPayload = ((AviationAerCorsiaRequestPayload) request.getPayload());

        AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload taskPayload =
                (AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.getVerificationReport().setVerificationData(taskActionPayload.getVerificationData());
        taskPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());

        aerRequestPayload.setVerificationReport(taskPayload.getVerificationReport());
        aerRequestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        aerRequestPayload
                .setVerificationSectionsCompleted(
                        taskActionPayload.getVerificationSectionsCompleted());
    }
}
