package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;


@Component
@RequiredArgsConstructor
public class AviationAerCorsiaAnnualOffsettingCreateActionHandler
        implements RequestAccountCreateActionHandler<AviationAerCorsiaAnnualOffsettingCreateActionPayload> {

    private final StartProcessRequestService startProcessRequestService;
    private final RequestService requestService;

    @Override
    public String process(Long accountId,
                          AviationAerCorsiaAnnualOffsettingCreateActionPayload payload, AppUser appUser) {

        Request aerRequest = requestService.findRequestById(payload.getRequestId());
        if(aerRequest == null){
            throw new UnsupportedOperationException("Missing aer for current request");
        }
        if(!aerRequest.getType().equals(RequestType.AVIATION_AER_CORSIA)){
            throw new UnsupportedOperationException("Request type mismatch. Expected AVIATION_AER_CORSIA");
        }
        AviationAerCorsiaRequestMetadata aerCorsiaRequestMetadata = (AviationAerCorsiaRequestMetadata) aerRequest.getMetadata();

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                .accountId(accountId)
                .requestPayload(AviationAerCorsiaAnnualOffsettingRequestPayload.builder()
                        .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .build())
                .requestMetadata(AviationAerCorsiaAnnualOffsettingRequestMetadata.builder()
                        .type(RequestMetadataType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                        .year(aerCorsiaRequestMetadata.getYear())
                        .build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING;
    }
}
