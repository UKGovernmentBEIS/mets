package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveAuthorityResponseRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerAuthorityResponseService;

@RequiredArgsConstructor
@Component
public class NerSaveAuthorityResponseActionHandler
    implements RequestTaskActionHandler<NerSaveAuthorityResponseRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NerAuthorityResponseService authorityResponseService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final NerSaveAuthorityResponseRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        authorityResponseService.applyAuthorityResponseSaveAction(requestTask, actionPayload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NER_SAVE_AUTHORITY_RESPONSE);
    }
}
