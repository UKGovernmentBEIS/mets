package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

@Service
@RequiredArgsConstructor
public class NERVerificationSubmitService {

    @Transactional
    public void applySaveAction(
            NERApplicationVerificationSaveRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {

        final Request request = requestTask.getRequest();
        NerRequestPayload requestPayload = ((NerRequestPayload) request.getPayload());

        NERApplicationVerificationSubmitRequestTaskPayload taskPayload =
                (NERApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.getVerificationReport().setVerificationData(taskActionPayload.getVerificationData());
        taskPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());

        requestPayload.setVerificationReport(taskPayload.getVerificationReport());
        requestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        requestPayload
                .setVerificationSectionsCompleted(
                        taskActionPayload.getVerificationSectionsCompleted());
        requestPayload.setVerificationAttachments(taskPayload.getVerificationAttachments());
    }
}
