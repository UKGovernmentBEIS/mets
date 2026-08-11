package uk.gov.pmrv.api.settings.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.settings.domain.dto.FeeRowDTO;
import uk.gov.pmrv.api.settings.domain.dto.FeeUpdateDTO;
import uk.gov.pmrv.api.settings.repository.SettingsFeeRepository;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFee;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;

@Service
@RequiredArgsConstructor
public class SettingsFeeService {

    private final SettingsFeeRepository settingsFeeRepository;
    private final SettingsFeeValidationService validationService;

    public List<FeeRowDTO> getFees(CompetentAuthorityEnum competentAuthority, AccountType accountType) {
        return settingsFeeRepository.findChangeableFeesByCompetentAuthority(competentAuthority)
                .stream()
                .filter(fee -> accountType == fee.getRequestType().getAccountType())
                .toList();
    }

    @Transactional
    public void updateFee(CompetentAuthorityEnum competentAuthority, AccountType accountType, Long id, FeeType feeType, FeeUpdateDTO dto) {
        LocalDate today = LocalDate.now();
        validationService.validateEffectiveDate(dto.getEffectiveDate(), today);

        PaymentFeeMethod feeMethod = validationService.validateAndGetFeeMethod(competentAuthority, accountType, id);
        PaymentFee existing = validationService.validateAndGetFee(feeMethod, feeType);
        validationService.validateNoConflictingScheduledUpdate(existing, dto.getEffectiveDate(), today);

        feeMethod.getFees().put(feeType, buildUpdatedFee(existing, dto, today));
    }

    @Transactional
    public void cancelScheduledFeeUpdate(CompetentAuthorityEnum competentAuthority, AccountType accountType, Long id, FeeType feeType) {
        PaymentFeeMethod feeMethod = validationService.validateAndGetFeeMethod(competentAuthority, accountType, id);
        PaymentFee existing = validationService.validateAndGetFee(feeMethod, feeType);
        validationService.validateScheduledChangeExists(existing);

        feeMethod.getFees().put(feeType, PaymentFee.builder()
                .amount(existing.getAmount())
                .changeable(existing.isChangeable())
                .build());
    }

    @Transactional
    public void applyScheduledFeeUpdates() {
        LocalDate today = LocalDate.now();
        settingsFeeRepository.findWithDueScheduledFees(today)
                .forEach(feeMethod -> feeMethod.getFees().replaceAll((feeType, fee) -> {
                    if (fee.getScheduledDate() != null && !fee.getScheduledDate().isAfter(today)
                            && fee.getScheduledAmount() != null) {
                        return PaymentFee.builder()
                                .amount(fee.getScheduledAmount())
                                .changeable(fee.isChangeable())
                                .build();
                    }
                    return fee;
                }));
    }

    private PaymentFee buildUpdatedFee(PaymentFee existing, FeeUpdateDTO dto, LocalDate today) {
        if (!dto.getEffectiveDate().isAfter(today)) {
            return PaymentFee.builder()
                    .amount(dto.getAmount())
                    .changeable(existing.isChangeable())
                    .build();
        }
        return PaymentFee.builder()
                .amount(existing.getAmount())
                .changeable(existing.isChangeable())
                .scheduledAmount(dto.getAmount())
                .scheduledDate(dto.getEffectiveDate())
                .build();
    }
}
