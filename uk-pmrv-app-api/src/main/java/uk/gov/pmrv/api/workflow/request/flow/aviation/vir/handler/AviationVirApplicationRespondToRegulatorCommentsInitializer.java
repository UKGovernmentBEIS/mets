package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.mapper.AviationVirMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;

@Service
public class AviationVirApplicationRespondToRegulatorCommentsInitializer implements InitializeRequestTaskHandler {

    private static final AviationVirMapper VIR_MAPPER = Mappers.getMapper(AviationVirMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        
        final AviationVirRequestPayload requestPayload = (AviationVirRequestPayload) request.getPayload();
        final Map<String, RegulatorImprovementResponse> regulatorImprovementResponses = requestPayload.getRegulatorReviewResponse()
                .getRegulatorImprovementResponses()
                .entrySet().stream().filter(entry -> entry.getValue().isImprovementRequired())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                .verificationData(requestPayload.getVerificationData())
                .operatorImprovementResponses(requestPayload.getOperatorImprovementResponses())
                .regulatorImprovementResponses(VIR_MAPPER.regulatorImprovementResponsesIgnoreComments(regulatorImprovementResponses))
                .virAttachments(requestPayload.getVirAttachments())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS);
    }
}
