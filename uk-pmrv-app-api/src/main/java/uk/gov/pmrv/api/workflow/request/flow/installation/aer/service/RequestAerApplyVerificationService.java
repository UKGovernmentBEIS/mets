package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationVerificationRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class RequestAerApplyVerificationService {

    @Transactional
    public void applySaveAction(
            AerSaveApplicationVerificationRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {

        final Request request = requestTask.getRequest();
        AerRequestPayload aerRequestPayload = ((AerRequestPayload) request.getPayload());

        AerApplicationVerificationSubmitRequestTaskPayload taskPayload =
                (AerApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();
        
        taskPayload.getVerificationReport().setVerificationData(taskActionPayload.getVerificationData());
        taskPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());

        aerRequestPayload.setVerificationReport(taskPayload.getVerificationReport());
        aerRequestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        aerRequestPayload
                .setVerificationSectionsCompleted(
                        taskActionPayload.getVerificationSectionsCompleted());
        aerRequestPayload.setVerificationAttachments(taskPayload.getVerificationAttachments());
    }
}
