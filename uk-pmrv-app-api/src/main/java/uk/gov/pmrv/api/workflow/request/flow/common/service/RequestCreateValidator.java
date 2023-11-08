package uk.gov.pmrv.api.workflow.request.flow.common.service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

public interface RequestCreateValidator {

    RequestCreateActionType getType();
}
