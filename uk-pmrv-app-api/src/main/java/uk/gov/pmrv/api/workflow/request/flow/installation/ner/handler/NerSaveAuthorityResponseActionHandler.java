package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveAuthorityResponseRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerAuthorityResponseService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class NerSaveAuthorityResponseActionHandler
    implements RequestTaskActionHandler<NerSaveAuthorityResponseRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NerAuthorityResponseService authorityResponseService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final NerSaveAuthorityResponseRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        authorityResponseService.applyAuthorityResponseSaveAction(requestTask, actionPayload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NER_SAVE_AUTHORITY_RESPONSE);
    }
}
