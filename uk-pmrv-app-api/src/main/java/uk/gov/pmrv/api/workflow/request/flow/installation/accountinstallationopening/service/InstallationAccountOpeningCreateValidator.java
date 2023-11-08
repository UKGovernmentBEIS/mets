package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByAccountValidator;

@Service
public class InstallationAccountOpeningCreateValidator implements RequestCreateByAccountValidator {

    @Override
    public RequestCreateValidationResult validateAction(Long accountId) {
        return RequestCreateValidationResult.builder().valid(accountId == null).build();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION;
    }
}
