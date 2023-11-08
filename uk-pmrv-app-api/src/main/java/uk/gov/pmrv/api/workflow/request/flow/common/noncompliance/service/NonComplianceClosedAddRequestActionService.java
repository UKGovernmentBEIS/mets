package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper.NonComplianceMapper;

@Service
@RequiredArgsConstructor
public class NonComplianceClosedAddRequestActionService {

    private static final NonComplianceMapper NON_COMPLIANCE_MAPPER = Mappers.getMapper(NonComplianceMapper.class);
    private final RequestService requestService;


    public void addRequestAction(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();

        final NonComplianceApplicationClosedRequestActionPayload actionPayload =
            NON_COMPLIANCE_MAPPER.toClosedRequestAction(requestPayload);

        final String assignee = requestPayload.getRegulatorAssignee();

        requestService.addActionToRequest(
            request,
            actionPayload,
            RequestActionType.NON_COMPLIANCE_APPLICATION_CLOSED,
            assignee
        );
    }
}
