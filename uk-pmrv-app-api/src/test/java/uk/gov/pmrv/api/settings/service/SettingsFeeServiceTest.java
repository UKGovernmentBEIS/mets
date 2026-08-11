package uk.gov.pmrv.api.settings.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.settings.domain.dto.FeeRowDTO;
import uk.gov.pmrv.api.settings.domain.dto.FeeUpdateDTO;
import uk.gov.pmrv.api.settings.repository.SettingsFeeRepository;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFee;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsFeeServiceTest {

    @InjectMocks
    private SettingsFeeService service;

    @Mock
    private SettingsFeeRepository settingsFeeRepository;

    @Mock
    private SettingsFeeValidationService validationService;

    @Test
    void getFees_installation_returnsOnlyInstallationFees() {
        List<FeeRowDTO> allFees = List.of(
                FeeRowDTO.builder().requestType(RequestType.PERMIT_ISSUANCE).feeType(FeeType.HSE).amount(new BigDecimal("1398")).build(),
                FeeRowDTO.builder().requestType(RequestType.EMP_ISSUANCE_UKETS).feeType(FeeType.FIXED).amount(new BigDecimal("500")).build()
        );
        when(settingsFeeRepository.findChangeableFeesByCompetentAuthority(CompetentAuthorityEnum.ENGLAND))
                .thenReturn(allFees);

        List<FeeRowDTO> result = service.getFees(CompetentAuthorityEnum.ENGLAND, AccountType.INSTALLATION);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getRequestType()).isEqualTo(RequestType.PERMIT_ISSUANCE);
        verify(settingsFeeRepository).findChangeableFeesByCompetentAuthority(CompetentAuthorityEnum.ENGLAND);
    }

    @Test
    void getFees_aviation_returnsOnlyAviationFees() {
        List<FeeRowDTO> allFees = List.of(
                FeeRowDTO.builder().requestType(RequestType.PERMIT_ISSUANCE).feeType(FeeType.HSE).amount(new BigDecimal("1398")).build(),
                FeeRowDTO.builder().requestType(RequestType.EMP_ISSUANCE_UKETS).feeType(FeeType.FIXED).amount(new BigDecimal("500")).build()
        );
        when(settingsFeeRepository.findChangeableFeesByCompetentAuthority(CompetentAuthorityEnum.ENGLAND))
                .thenReturn(allFees);

        List<FeeRowDTO> result = service.getFees(CompetentAuthorityEnum.ENGLAND, AccountType.AVIATION);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getRequestType()).isEqualTo(RequestType.EMP_ISSUANCE_UKETS);
        verify(settingsFeeRepository).findChangeableFeesByCompetentAuthority(CompetentAuthorityEnum.ENGLAND);
    }

    @Test
    void getFees_emptyResult_returnsEmptyList() {
        when(settingsFeeRepository.findChangeableFeesByCompetentAuthority(CompetentAuthorityEnum.WALES))
                .thenReturn(List.of());

        List<FeeRowDTO> result = service.getFees(CompetentAuthorityEnum.WALES, AccountType.INSTALLATION);

        assertThat(result).isEmpty();
    }

    @Test
    void updateFee_effectiveDateToday_updatesAmountImmediately() {
        LocalDate today = LocalDate.now();
        FeeUpdateDTO dto = FeeUpdateDTO.builder().amount(new BigDecimal("1500")).effectiveDate(today).build();
        PaymentFee existing = PaymentFee.builder().amount(new BigDecimal("1000")).build();
        PaymentFeeMethod feeMethod = feeMethodWith(FeeType.FIXED, existing);
        when(validationService.validateAndGetFeeMethod(CompetentAuthorityEnum.ENGLAND, AccountType.AVIATION, 1L))
                .thenReturn(feeMethod);
        when(validationService.validateAndGetFee(feeMethod, FeeType.FIXED)).thenReturn(existing);

        service.updateFee(CompetentAuthorityEnum.ENGLAND, AccountType.AVIATION, 1L, FeeType.FIXED, dto);

        PaymentFee updated = feeMethod.getFees().get(FeeType.FIXED);
        assertThat(updated.getAmount()).isEqualByComparingTo("1500");
        assertThat(updated.getScheduledAmount()).isNull();
        assertThat(updated.getScheduledDate()).isNull();
        verify(validationService).validateEffectiveDate(eq(today), any(LocalDate.class));
    }

    @Test
    void updateFee_effectiveDateFuture_setsScheduledFields() {
        LocalDate futureDate = LocalDate.now().plusDays(30);
        FeeUpdateDTO dto = FeeUpdateDTO.builder().amount(new BigDecimal("2000")).effectiveDate(futureDate).build();
        PaymentFee existing = PaymentFee.builder().amount(new BigDecimal("1000")).build();
        PaymentFeeMethod feeMethod = feeMethodWith(FeeType.FIXED, existing);
        when(validationService.validateAndGetFeeMethod(CompetentAuthorityEnum.ENGLAND, AccountType.AVIATION, 1L))
                .thenReturn(feeMethod);
        when(validationService.validateAndGetFee(feeMethod, FeeType.FIXED)).thenReturn(existing);

        service.updateFee(CompetentAuthorityEnum.ENGLAND, AccountType.AVIATION, 1L, FeeType.FIXED, dto);

        PaymentFee updated = feeMethod.getFees().get(FeeType.FIXED);
        assertThat(updated.getAmount()).isEqualByComparingTo("1000");
        assertThat(updated.getScheduledAmount()).isEqualByComparingTo("2000");
        assertThat(updated.getScheduledDate()).isEqualTo(futureDate);
        verify(validationService).validateEffectiveDate(eq(futureDate), any(LocalDate.class));
    }

    @Test
    void updateFee_immediateDateWithExistingSchedule_delegatesValidationToService() {
        LocalDate today = LocalDate.now();
        FeeUpdateDTO dto = FeeUpdateDTO.builder().amount(new BigDecimal("1500")).effectiveDate(today).build();
        PaymentFee existing = PaymentFee.builder()
                .amount(new BigDecimal("1000"))
                .scheduledAmount(new BigDecimal("1200"))
                .scheduledDate(today.plusDays(5))
                .build();
        PaymentFeeMethod feeMethod = feeMethodWith(FeeType.FIXED, existing);
        when(validationService.validateAndGetFeeMethod(CompetentAuthorityEnum.ENGLAND, AccountType.AVIATION, 1L))
                .thenReturn(feeMethod);
        when(validationService.validateAndGetFee(feeMethod, FeeType.FIXED)).thenReturn(existing);

        service.updateFee(CompetentAuthorityEnum.ENGLAND, AccountType.AVIATION, 1L, FeeType.FIXED, dto);

        verify(validationService).validateNoConflictingScheduledUpdate(eq(existing), eq(today), any(LocalDate.class));
    }

    @Test
    void cancelScheduledFeeUpdate_clearsScheduledFields() {
        PaymentFee existing = PaymentFee.builder()
                .amount(new BigDecimal("1000"))
                .scheduledAmount(new BigDecimal("1500"))
                .scheduledDate(LocalDate.now().plusDays(10))
                .build();
        PaymentFeeMethod feeMethod = feeMethodWith(FeeType.FIXED, existing);
        when(validationService.validateAndGetFeeMethod(CompetentAuthorityEnum.ENGLAND, AccountType.AVIATION, 1L))
                .thenReturn(feeMethod);
        when(validationService.validateAndGetFee(feeMethod, FeeType.FIXED)).thenReturn(existing);

        service.cancelScheduledFeeUpdate(CompetentAuthorityEnum.ENGLAND, AccountType.AVIATION, 1L, FeeType.FIXED);

        PaymentFee updated = feeMethod.getFees().get(FeeType.FIXED);
        assertThat(updated.getAmount()).isEqualByComparingTo("1000");
        assertThat(updated.getScheduledAmount()).isNull();
        assertThat(updated.getScheduledDate()).isNull();
        verify(validationService).validateScheduledChangeExists(existing);
    }

    @Test
    void applyScheduledFeeUpdates_promotesScheduledAmountToAmount() {
        LocalDate today = LocalDate.now();
        PaymentFee fee = PaymentFee.builder()
                .amount(new BigDecimal("1000"))
                .scheduledAmount(new BigDecimal("1500"))
                .scheduledDate(today)
                .build();
        PaymentFeeMethod feeMethod = feeMethodWith(FeeType.FIXED, fee);
        when(settingsFeeRepository.findWithDueScheduledFees(any(LocalDate.class))).thenReturn(List.of(feeMethod));

        service.applyScheduledFeeUpdates();

        PaymentFee updated = feeMethod.getFees().get(FeeType.FIXED);
        assertThat(updated.getAmount()).isEqualByComparingTo("1500");
        assertThat(updated.getScheduledAmount()).isNull();
        assertThat(updated.getScheduledDate()).isNull();
    }

    @Test
    void applyScheduledFeeUpdates_noFeesDue_doesNothing() {
        when(settingsFeeRepository.findWithDueScheduledFees(any(LocalDate.class))).thenReturn(List.of());

        service.applyScheduledFeeUpdates();

        verify(settingsFeeRepository).findWithDueScheduledFees(any(LocalDate.class));
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
