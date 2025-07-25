package uk.gov.pmrv.api.workflow.request.core.service;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Set;

public interface InitializeRequestTaskHandler {

    RequestTaskPayload initializePayload(Request request);

    Set<RequestTaskType> getRequestTaskTypes();
}
