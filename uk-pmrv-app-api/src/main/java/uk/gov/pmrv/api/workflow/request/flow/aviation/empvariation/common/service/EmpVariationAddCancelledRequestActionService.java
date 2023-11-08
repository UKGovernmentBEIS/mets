package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class EmpVariationAddCancelledRequestActionService {

	private final RequestService requestService;

    public void add(final String requestId, final RoleType userRole) {

        final Request request = requestService.findRequestById(requestId);
        final String assignee = userRole == RoleType.OPERATOR ?
            request.getPayload().getOperatorAssignee() : request.getPayload().getRegulatorAssignee();

        requestService.addActionToRequest(request,
            null,
            RequestActionType.EMP_VARIATION_APPLICATION_CANCELLED,
            assignee);
    }
}
