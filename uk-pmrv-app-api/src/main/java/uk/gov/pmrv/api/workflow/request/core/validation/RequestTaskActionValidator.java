package uk.gov.pmrv.api.workflow.request.core.validation;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

import java.util.Set;

public interface RequestTaskActionValidator {
    
    RequestTaskActionValidationResult validate(RequestTask requestTask);

    Set<RequestTaskActionType> getTypes();
}
