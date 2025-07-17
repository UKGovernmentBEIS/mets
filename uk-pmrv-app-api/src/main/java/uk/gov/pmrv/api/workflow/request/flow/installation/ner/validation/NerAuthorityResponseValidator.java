package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.AuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.GrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.AuthorityResponseType;

@Service
@Validated
@RequiredArgsConstructor
public class NerAuthorityResponseValidator {

    private final AllowanceAllocationValidator allowanceAllocationValidator;

    public void validateAuthorityResponse(@NotNull @Valid final NerAuthorityResponseRequestTaskPayload payload) {

        final AuthorityResponse authorityResponse = payload.getAuthorityResponse();
        if (authorityResponse.getType() == AuthorityResponseType.INVALID) {
            return;
        }

        boolean isValid = allowanceAllocationValidator.isValid(((GrantAuthorityResponse) authorityResponse)
                .getPreliminaryAllocations());

        if (!isValid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}
