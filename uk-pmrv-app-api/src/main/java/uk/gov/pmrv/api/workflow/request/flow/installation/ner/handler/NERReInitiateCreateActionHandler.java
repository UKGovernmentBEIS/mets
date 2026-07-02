package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERInitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERRegulatorReviewSubmitService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NERReInitiateCreateActionHandler implements RequestAccountCreateActionHandler<NERRequestCreateActionPayload> {

    private final RequestService requestService;
    private final StartProcessRequestService startProcessRequestService;
    private final NERRegulatorReviewSubmitService nerRegulatorReviewSubmitService;

    @Override
    public String process(Long accountId, NERRequestCreateActionPayload payload, AppUser appUser) {
        Request request = requestService.findRequestById(payload.getRequestId());
        NERRequestMetadata requestMetadata = (NERRequestMetadata) request.getMetadata();

        requestMetadata.setNerInitiationType(NERInitiationType.RE_INITIATED);
        nerRegulatorReviewSubmitService.prepareRequestPayloadForReopening((NerRequestPayload) request.getPayload());

        startProcessRequestService.reStartProcess(request, Map.of(BpmnProcessConstants.NER_INITIATION_TYPE, NERInitiationType.RE_INITIATED,
                BpmnProcessConstants.SKIP_PAYMENT,true));

        requestService.addActionToRequest(
                request,
                null,
                RequestActionType.NER_APPLICATION_RE_INITIATED,
                appUser.getUserId());

        return payload.getRequestId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.NER_RE_INITIATE;
    }
}
