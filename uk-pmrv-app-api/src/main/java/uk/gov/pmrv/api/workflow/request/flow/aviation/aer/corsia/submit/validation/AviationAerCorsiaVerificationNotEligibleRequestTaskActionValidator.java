package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.validation.AviationAerVerificationNotEligibleRequestTaskActionValidator;

import java.util.Set;

@Service
public class AviationAerCorsiaVerificationNotEligibleRequestTaskActionValidator
        extends AviationAerVerificationNotEligibleRequestTaskActionValidator {

    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of(
            RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_VERIFICATION,
            RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_AMENDS_VERIFICATION
        );
    }
}
