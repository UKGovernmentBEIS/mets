package uk.gov.pmrv.api.workflow.request.flow.installation.common.validation;

import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitPayloadGroupDecidable;

public interface PermitReviewDeterminationAndDecisionsValidator<T extends PermitPayloadGroupDecidable> {

    boolean isValid(T decidable);

    @NotNull
    DeterminationType getType();
    
    @NotNull
    RequestType getRequestType();
    
}
