package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ALRApplicationSubmitInitializer implements InitializeRequestTaskHandler {
    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final ALRRequestPayload requestPayload =
                (ALRRequestPayload) request.getPayload();

        final ALRApplicationSubmitRequestTaskPayload taskPayload;


//        Get verification body id in case of verification performed
        Long verificationBodyId = !ObjectUtils.isEmpty(requestPayload.getVerificationReport()) ?
                requestPayload.getVerificationReport().getVerificationBodyId() :
                null;

        taskPayload = ALRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                .alr(requestPayload.getAlr())
                .alrAttachments(requestPayload.getAlrAttachments())
                .alrSectionsCompleted(requestPayload.getAlrSectionsCompleted())
                .verificationPerformed(requestPayload.isVerificationPerformed())
                .verificationSectionsCompleted(requestPayload.getVerificationSectionsCompleted())
                .verificationBodyId(verificationBodyId)
                .build();

        return taskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.ALR_APPLICATION_SUBMIT);
    }
}
