package uk.gov.pmrv.api.workflow.request.flow.installation.noncompliance.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@Component
@RequiredArgsConstructor
public class NonComplianceCreateActionHandler implements RequestCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(final Long accountId,
                          final RequestCreateActionType type,
                          final RequestCreateActionEmptyPayload payload,
                          final PmrvUser pmrvUser) {

        final RequestParams requestParams = RequestParams.builder()
            .accountId(accountId)
            .type(RequestType.NON_COMPLIANCE)
            .requestPayload(NonComplianceRequestPayload.builder()
				.payloadType(RequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
				.regulatorAssignee(pmrvUser.getUserId())
				.build())
            .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.NON_COMPLIANCE;
    }

}
