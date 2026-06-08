package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

public interface RequestRecallEmailNotificationHandler {

    RequestType getRequestType();

    void sendRecallEmailNotification(Request request);
}