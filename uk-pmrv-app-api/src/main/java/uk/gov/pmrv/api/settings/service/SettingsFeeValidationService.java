package uk.gov.pmrv.api.settings.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.settings.repository.SettingsFeeRepository;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFee;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;

@Service
@RequiredArgsConstructor
public class SettingsFeeValidationService {

    private final SettingsFeeRepository settingsFeeRepository;

    public void validateEffectiveDate(LocalDate effectiveDate, LocalDate today) {
        if (effectiveDate.isBefore(today)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_FORMAT);
        }
    }

    public PaymentFeeMethod validateAndGetFeeMethod(CompetentAuthorityEnum competentAuthority, AccountType accountType, Long id) {
        PaymentFeeMethod feeMethod = settingsFeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        if (feeMethod.getCompetentAuthority() != competentAuthority) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (feeMethod.getRequestType().getAccountType() != accountType) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return feeMethod;
    }

    public PaymentFee validateAndGetFee(PaymentFeeMethod feeMethod, FeeType feeType) {
        PaymentFee fee = feeMethod.getFees().get(feeType);
        if (fee == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        if (!fee.isChangeable()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return fee;
    }

    public void validateScheduledChangeExists(PaymentFee fee) {
        if (fee.getScheduledDate() == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    public void validateNoConflictingScheduledUpdate(PaymentFee existing, LocalDate effectiveDate, LocalDate today) {
        if (!effectiveDate.isAfter(today) && existing.getScheduledDate() != null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_FORMAT);
        }
    }
}
