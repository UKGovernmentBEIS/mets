package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.mapper.AirMapper;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AirApplicationRespondToRegulatorCommentsInitializer implements InitializeRequestTaskHandler {
    
    private static final AirMapper AIR_MAPPER = Mappers.getMapper(AirMapper.class);

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final AirRequestPayload requestPayload = (AirRequestPayload) request.getPayload();
        final Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses =
            requestPayload.getRegulatorReviewResponse().getRegulatorImprovementResponses()
                .entrySet().stream()
                .filter(entry -> entry.getValue().getImprovementRequired())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return AirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
            .airImprovements(requestPayload.getAirImprovements())
            .operatorImprovementResponses(requestPayload.getOperatorImprovementResponses())
            .regulatorImprovementResponses(AIR_MAPPER.regulatorImprovementResponsesIgnoreComments(regulatorImprovementResponses))
            .airAttachments(requestPayload.getAirAttachments())
            .reviewAttachments(requestPayload.getReviewAttachments())
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AIR_RESPOND_TO_REGULATOR_COMMENTS);
    }
}
