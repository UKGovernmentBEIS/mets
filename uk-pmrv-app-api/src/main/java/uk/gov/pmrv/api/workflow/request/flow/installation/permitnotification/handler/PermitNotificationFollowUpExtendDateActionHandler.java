package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpExtendDateRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationSendEventService;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PermitNotificationFollowUpExtendDateActionHandler 
    implements RequestTaskActionHandler<PermitNotificationFollowUpExtendDateRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitNotificationSendEventService eventService;

    @Override
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final AppUser appUser,
                        final PermitNotificationFollowUpExtendDateRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final LocalDate previousDueDate = requestTask.getDueDate();
        final LocalDate dueDate = actionPayload.getDueDate();
        
        // validate
        if (!dueDate.isAfter(previousDueDate)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
        
        // send event
        final String requestId = requestTask.getRequest().getId();
        eventService.extendTimer(requestId, dueDate);        
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE);
    }
}
