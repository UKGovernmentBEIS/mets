package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.Year;
import java.util.List;


@Component
@RequiredArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingCreateActionHandler
        implements RequestAccountCreateActionHandler<AviationAerCorsia3YearPeriodCreateActionPayload> {

    private final StartProcessRequestService startProcessRequestService;
    private final RequestService requestService;

    @Override
    public String process(Long accountId,
                          AviationAerCorsia3YearPeriodCreateActionPayload payload, AppUser appUser) {

        Request aerRequest = requestService.findRequestById(payload.getRequestId());

        AviationAerCorsiaRequestMetadata aerCorsiaRequestMetadata =
                (AviationAerCorsiaRequestMetadata) aerRequest.getMetadata();
        Year aerYear = aerCorsiaRequestMetadata.getYear();

        List<Year> schemeYears = List.of(aerYear.minusYears(2), aerYear.minusYears(1), aerYear);

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING)
                .accountId(accountId)
                .requestPayload(AviationAerCorsia3YearPeriodOffsettingRequestPayload.builder()
                        .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .build())
                .requestMetadata(AviationAerCorsia3YearPeriodOffsettingRequestMetadata.builder()
                        .type(RequestMetadataType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING)
                        .year(aerYear)
                        .currentAerId(aerRequest.getId())
                        .years(schemeYears)
                        .build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING;
    }
}
