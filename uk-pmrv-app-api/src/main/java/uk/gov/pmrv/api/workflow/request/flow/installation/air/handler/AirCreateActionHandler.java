package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirCreateImprovementDataService;

@Component
@RequiredArgsConstructor
public class AirCreateActionHandler implements RequestCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final PermitQueryService permitQueryService;
    private final AirCreateImprovementDataService dataService;
    private final DateService dateService;
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(final Long accountId,
                          final RequestCreateActionType type,
                          final RequestCreateActionEmptyPayload payload,
                          final PmrvUser pmrvUser) {
        
        final PermitContainer permitContainer = permitQueryService.getPermitContainerByAccountId(accountId);
        final Permit permit = permitContainer.getPermit();
        final List<AirImprovement> airImprovements = dataService.createImprovementData(permit);
        final Map<Integer, AirImprovement> airImprovementMap = IntStream.range(1, airImprovements.size() + 1)
            .boxed()
            .collect(Collectors.toMap(i -> i, i -> airImprovements.get(i - 1)));
        
        final RequestParams requestParams = RequestParams.builder()
            .accountId(accountId)
            .type(RequestType.AIR)
            .requestPayload(AirRequestPayload.builder()
                .payloadType(RequestPayloadType.AIR_REQUEST_PAYLOAD)
                .airImprovements(airImprovementMap)
                .build())
            .requestMetadata(AirRequestMetadata.builder()
                .type(RequestMetadataType.AIR)
                .year(dateService.getYear())
                .build())
            .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.AIR;
    }

}
