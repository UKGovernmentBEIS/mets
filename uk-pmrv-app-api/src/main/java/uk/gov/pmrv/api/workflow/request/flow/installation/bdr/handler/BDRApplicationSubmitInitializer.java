package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BDRApplicationSubmitInitializer  implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final BDRRequestPayload requestPayload =
                (BDRRequestPayload) request.getPayload();

        final BDRApplicationSubmitRequestTaskPayload taskPayload;

        // Get verification body id in case of verification performed
        Long verificationBodyId = !ObjectUtils.isEmpty(requestPayload.getVerificationReport()) ?
            requestPayload.getVerificationReport().getVerificationBodyId() :
            null;

        taskPayload = BDRApplicationSubmitRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                    .bdr(requestPayload.getBdr())
                    .bdrAttachments(requestPayload.getBdrAttachments())
                    .bdrSectionsCompleted(requestPayload.getBdrSectionsCompleted())
                    .verificationPerformed(requestPayload.isVerificationPerformed())
                    .verificationSectionsCompleted(requestPayload.getVerificationSectionsCompleted())
                    .verificationBodyId(verificationBodyId)
                    .build();

        return taskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.BDR_APPLICATION_SUBMIT);
    }
}
