package uk.gov.pmrv.api.workflow.request.core.validation;

import java.util.Set;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

public interface RequestTaskActionValidator {
    
    RequestTaskActionValidationResult validate(RequestTask requestTask);

    Set<RequestTaskActionType> getTypes();
}
