package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@Component
@RequiredArgsConstructor
public class AviationDoECorsiaCreateActionHandler  implements RequestAccountCreateActionHandler<ReportRelatedRequestCreateActionPayload> {

    private final RequestQueryService requestQueryService;
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, ReportRelatedRequestCreateActionPayload payload,
                          AppUser appUser) {
        final RequestDetailsDTO aerRequestDetails = requestQueryService.findRequestDetailsById(payload.getRequestId());
        final AviationAerRequestMetadata aerMetadata = (AviationAerRequestMetadata) aerRequestDetails.getRequestMetadata();
        final RequestParams requestParams = RequestParams.builder()
                .type(RequestType.AVIATION_DOE_CORSIA)
                .accountId(accountId)
                .requestMetadata(AviationDoECorsiaRequestMetadata.builder()
                        .type(RequestMetadataType.AVIATION_DOE_CORSIA)
                        .year(aerMetadata.getYear())
                        .isExempted(aerMetadata.isExempted())
                        .build())
                .requestPayload(AviationDoECorsiaRequestPayload.builder()
                        .payloadType(RequestPayloadType.AVIATION_DOE_CORSIA_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .reportingYear(aerMetadata.getYear())
                        .initiatorRequest(aerMetadata.getInitiatorRequest())
                        .build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.AVIATION_DOE_CORSIA;
    }
}
