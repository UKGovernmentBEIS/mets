package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NerMapper;

@Service
@RequiredArgsConstructor
public class NerApplicationAddProceededToAuthorityRequestActionService {

    private static final NerMapper NER_MAPPER = Mappers.getMapper(NerMapper.class);

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    public void proceedToAuthority(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final String assignee = request.getPayload().getRegulatorAssignee();

        final NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();
        final NerApplicationProceededToAuthorityRequestActionPayload actionPayload =
            NER_MAPPER.toNerApplicationProceededToAuthority(requestPayload);

        final DecisionNotification notification =
            requestPayload.getDecisionNotification();
        
        // in case the ReviewOutcome is COMPLETED, no notification will be sent
        if (notification != null) {
            final Map<String, RequestActionUserInfo> usersInfo =
                requestActionUserInfoResolver.getUsersInfo(notification.getOperators(), notification.getSignatory(),
                    request);
            actionPayload.setUsersInfo(usersInfo);   
        }

        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.NER_APPLICATION_PROCEEDED_TO_AUTHORITY,
            assignee);
    }
}
