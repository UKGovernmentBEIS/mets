package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.mapper.VirMapper;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VirApplicationRespondToRegulatorCommentsInitializer implements InitializeRequestTaskHandler {

    private static final VirMapper VIR_MAPPER = Mappers.getMapper(VirMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final VirRequestPayload requestPayload = (VirRequestPayload) request.getPayload();
        final Map<String, RegulatorImprovementResponse> regulatorImprovementResponses = requestPayload.getRegulatorReviewResponse()
                .getRegulatorImprovementResponses()
                .entrySet().stream().filter(entry -> entry.getValue().isImprovementRequired())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return VirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                .verificationData(requestPayload.getVerificationData())
                .operatorImprovementResponses(requestPayload.getOperatorImprovementResponses())
                .regulatorImprovementResponses(VIR_MAPPER.regulatorImprovementResponsesIgnoreComments(regulatorImprovementResponses))
                .virAttachments(requestPayload.getVirAttachments())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.VIR_RESPOND_TO_REGULATOR_COMMENTS);
    }
}
