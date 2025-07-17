package uk.gov.pmrv.api.reporting.validation;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AerVerificationReportExistenceValidator {

    private final InstallationAccountQueryService installationAccountQueryService;

    public void validate(AerContainer aerContainer, Long accountId) {
        EmitterType emitterType = installationAccountQueryService
                .getInstallationAccountInfoDTOById(accountId).getEmitterType();

        if(emitterType.equals(EmitterType.GHGE) && ObjectUtils.isEmpty(aerContainer.getVerificationReport())) {
            throw new BusinessException(MetsErrorCode.INVALID_AER,
                    AerValidationResult.builder().valid(false).aerViolations(List.of(
                            new AerViolation(AerVerificationReport.class.getSimpleName(),
                                    AerViolation.AerViolationMessage.NO_VERIFICATION_REPORT_FOUND))).build()
            );
        }
    }
}
