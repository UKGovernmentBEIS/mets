package uk.gov.pmrv.api.workflow.request.flow.aviation.noncompliance.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@Component
@RequiredArgsConstructor
public class AviationNonComplianceCreateActionHandler implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(final Long accountId,
                          final RequestCreateActionEmptyPayload payload,
                          final AppUser appUser) {

        final RequestParams requestParams = RequestParams.builder()
            .accountId(accountId)
            .type(RequestType.AVIATION_NON_COMPLIANCE)
            .requestPayload(NonComplianceRequestPayload.builder()
				.payloadType(RequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
				.regulatorAssignee(appUser.getUserId())
				.build())
            .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.AVIATION_NON_COMPLIANCE;
    }

}
