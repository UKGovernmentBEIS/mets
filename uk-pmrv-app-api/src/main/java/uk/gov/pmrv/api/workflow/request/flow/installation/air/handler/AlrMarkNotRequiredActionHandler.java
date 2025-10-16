package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AlrApplicationMarkNotRequiredRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AlrMarkNotRequiredDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class AlrMarkNotRequiredActionHandler {

    private final RequestService requestService;
    private final WorkflowService workflowService;
    private static final ALRMapper alrMapper = Mappers.getMapper(ALRMapper.class);

    @Transactional
    public void process(final String requestId,
                        final AppUser appUser,AlrMarkNotRequiredDetails markNotRequiredDetails){

        Request request = requestService.findRequestById(requestId);
        AlrApplicationMarkNotRequiredRequestActionPayload requestPayload =
                alrMapper.toAlrApplicationMarkNotRequiredRequestActionPayload(markNotRequiredDetails);
        requestService.addActionToRequest(request,requestPayload, RequestActionType.ALR_APPLICATION_MARK_NOT_REQUIRED,appUser.getUserId());
        workflowService.sendEvent(request.getId(), BpmnProcessConstants.ALR_MARK_NOT_REQUIRED, new HashMap<>());
    }

}
