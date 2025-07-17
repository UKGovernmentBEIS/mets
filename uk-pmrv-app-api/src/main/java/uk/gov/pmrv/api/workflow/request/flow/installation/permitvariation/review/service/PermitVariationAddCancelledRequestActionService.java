package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PermitVariationAddCancelledRequestActionService {

    private final RequestService requestService;

    public void add(final String requestId, final String userRole) {

        final Request request = requestService.findRequestById(requestId);
        final String assignee = RoleTypeConstants.OPERATOR.equals(userRole) ?
            request.getPayload().getOperatorAssignee() : request.getPayload().getRegulatorAssignee();

        requestService.addActionToRequest(request,
            null,
            RequestActionType.PERMIT_VARIATION_APPLICATION_CANCELLED,
            assignee);
    }
}
