package uk.gov.pmrv.api.settings.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.settings.repository.SettingsFeeRepository;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFee;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsFeeValidationServiceTest {

    @InjectMocks
    private SettingsFeeValidationService validationService;

    @Mock
    private SettingsFeeRepository settingsFeeRepository;

    @Test
    void validateEffectiveDate_pastDate_throwsInvalidRequestFormat() {
        LocalDate today = LocalDate.now();
        LocalDate date = today.minusDays(1);
        assertThatThrownBy(() -> validationService.validateEffectiveDate(date, today))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_REQUEST_FORMAT);
    }

    @Test
    void validateEffectiveDate_today_doesNotThrow() {
        LocalDate today = LocalDate.now();
        validationService.validateEffectiveDate(today, today);
    }

    @Test
    void validateEffectiveDate_futureDate_doesNotThrow() {
        LocalDate today = LocalDate.now();
        validationService.validateEffectiveDate(today.plusDays(1), today);
    }

    @Test
    void validateAndGetFeeMethod_notFound_throwsResourceNotFound() {
        when(settingsFeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validationService.validateAndGetFeeMethod(CompetentAuthorityEnum.ENGLAND, AccountType.AVIATION, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    void validateAndGetFeeMethod_differentCa_throwsForbidden() {
        PaymentFeeMethod feeMethod = feeMethodWith(FeeType.FIXED, PaymentFee.builder().amount(new BigDecimal("1000")).build());
        when(settingsFeeRepository.findById(1L)).thenReturn(Optional.of(feeMethod));

        assertThatThrownBy(() -> validationService.validateAndGetFeeMethod(CompetentAuthorityEnum.SCOTLAND, AccountType.AVIATION, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    void validateAndGetFeeMethod_differentAccountType_throwsForbidden() {
        PaymentFeeMethod feeMethod = feeMethodWith(FeeType.FIXED, PaymentFee.builder().amount(new BigDecimal("1000")).build());
        when(settingsFeeRepository.findById(1L)).thenReturn(Optional.of(feeMethod));

        assertThatThrownBy(() -> validationService.validateAndGetFeeMethod(CompetentAuthorityEnum.ENGLAND, AccountType.INSTALLATION, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    void validateAndGetFeeMethod_found_returnsFeeMethod() {
        PaymentFeeMethod feeMethod = feeMethodWith(FeeType.FIXED, PaymentFee.builder().amount(new BigDecimal("1000")).build());
        when(settingsFeeRepository.findById(1L)).thenReturn(Optional.of(feeMethod));

        PaymentFeeMethod result = validationService.validateAndGetFeeMethod(CompetentAuthorityEnum.ENGLAND, AccountType.AVIATION, 1L);

        assertThat(result).isEqualTo(feeMethod);
    }

    @Test
    void validateAndGetFee_feeTypeNotInMap_throwsResourceNotFound() {
        PaymentFeeMethod feeMethod = feeMethodWith(FeeType.HSE, PaymentFee.builder().amount(new BigDecimal("1000")).build());

        assertThatThrownBy(() -> validationService.validateAndGetFee(feeMethod, FeeType.FIXED))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    void validateAndGetFee_notChangeable_throwsForbidden() {
        PaymentFeeMethod feeMethod = feeMethodWith(FeeType.FIXED,
                PaymentFee.builder().amount(new BigDecimal("1000")).changeable(false).build());

        assertThatThrownBy(() -> validationService.validateAndGetFee(feeMethod, FeeType.FIXED))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    void validateAndGetFee_valid_returnsFee() {
        PaymentFee fee = PaymentFee.builder().amount(new BigDecimal("1000")).build();
        PaymentFeeMethod feeMethod = feeMethodWith(FeeType.FIXED, fee);

        PaymentFee result = validationService.validateAndGetFee(feeMethod, FeeType.FIXED);

        assertThat(result).isEqualTo(fee);
    }

    @Test
    void validateNoConflictingScheduledUpdate_immediateDateWithPendingSchedule_throwsInvalidRequestFormat() {
        LocalDate today = LocalDate.now();
        PaymentFee fee = PaymentFee.builder()
                .amount(new BigDecimal("1000"))
                .scheduledAmount(new BigDecimal("1500"))
                .scheduledDate(today.plusDays(10))
                .build();

        assertThatThrownBy(() -> validationService.validateNoConflictingScheduledUpdate(fee, today, today))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_REQUEST_FORMAT);
    }

    @Test
    void validateNoConflictingScheduledUpdate_immediateDateWithNoSchedule_doesNotThrow() {
        LocalDate today = LocalDate.now();
        PaymentFee fee = PaymentFee.builder().amount(new BigDecimal("1000")).build();

        validationService.validateNoConflictingScheduledUpdate(fee, today, today);
    }

    @Test
    void validateNoConflictingScheduledUpdate_futureDateWithPendingSchedule_doesNotThrow() {
        LocalDate today = LocalDate.now();
        PaymentFee fee = PaymentFee.builder()
                .amount(new BigDecimal("1000"))
                .scheduledAmount(new BigDecimal("1500"))
                .scheduledDate(today.plusDays(10))
                .build();

        validationService.validateNoConflictingScheduledUpdate(fee, today.plusDays(30), today);
    }

    @Test
    void validateScheduledChangeExists_noScheduledDate_throwsResourceNotFound() {
        PaymentFee fee = PaymentFee.builder().amount(new BigDecimal("1000")).build();

        assertThatThrownBy(() -> validationService.validateScheduledChangeExists(fee))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    void validateScheduledChangeExists_withScheduledDate_doesNotThrow() {
        PaymentFee fee = PaymentFee.builder()
                .amount(new BigDecimal("1000"))
                .scheduledAmount(new BigDecimal("1500"))
                .scheduledDate(LocalDate.now().plusDays(10))
                .build();

        validationService.validateScheduledChangeExists(fee);
    }

    private PaymentFeeMethod feeMethodWith(FeeType feeType, PaymentFee fee) {
        EnumMap<FeeType, PaymentFee> fees = new EnumMap<>(FeeType.class);
        fees.put(feeType, fee);
        return PaymentFeeMethod.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .requestType(RequestType.EMP_ISSUANCE_UKETS)
                .type(FeeMethodType.STANDARD)
                .fees(fees)
                .build();
    }
}
