package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@Service
public class EmpVariationCorsiaCreateValidator extends RequestCreateAccountRelatedValidator {

	public EmpVariationCorsiaCreateValidator(final RequestCreateValidatorService requestCreateValidatorService) {
        super(requestCreateValidatorService);
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.EMP_VARIATION_CORSIA;
    }

    @Override
    public Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(AviationAccountStatus.LIVE);
    }

    @Override
    public Set<RequestType> getMutuallyExclusiveRequests() {
        return Set.of(RequestType.EMP_VARIATION_CORSIA); 
    }
}
