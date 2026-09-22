package uk.gov.pmrv.api.workflow.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFee;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@ExtendWith(MockitoExtension.class)
class PaymentFeeMethodServiceTest {

    @Mock
    private PaymentFeeMethodRepository paymentFeeMethodRepository;

    private PaymentFeeMethodService service;

    @BeforeEach
    void setUp() {
        service = new PaymentFeeMethodService(paymentFeeMethodRepository);
    }

    @Test
    void getFeeMethodType_shouldReturnFeeMethodType_whenConfigurationExists() {
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
                .competentAuthority(CompetentAuthorityEnum.WALES)
                .requestType(RequestType.PERMIT_VARIATION)
                .type(FeeMethodType.STANDARD)
                .build();

        when(paymentFeeMethodRepository
                .findByCompetentAuthorityAndRequestType(
                        CompetentAuthorityEnum.WALES,
                        RequestType.PERMIT_VARIATION
                ))
                .thenReturn(Optional.of(paymentFeeMethod));

        Optional<FeeMethodType> result = service.getFeeMethodType(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION
        );

        assertThat(result)
                .contains(FeeMethodType.STANDARD);
    }

    @Test
    void getFeeMethodType_shouldReturnEmpty_whenConfigurationDoesNotExist() {
        when(paymentFeeMethodRepository
                .findByCompetentAuthorityAndRequestType(
                        CompetentAuthorityEnum.WALES,
                        RequestType.PERMIT_VARIATION
                ))
                .thenReturn(Optional.empty());

        Optional<FeeMethodType> result = service.getFeeMethodType(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION
        );

        assertThat(result).isEmpty();
    }

    @Test
    void isZeroNonChangeableFeeConfigured_shouldReturnTrue_whenFixedFeeIsZeroAndNonChangeable() {
        PaymentFeeMethod paymentFeeMethod = buildPaymentFeeMethod(
                Map.of(
                        FeeType.FIXED,
                        new PaymentFee(
                                BigDecimal.ZERO,
                                false,
                                null,
                                null
                        )
                )
        );

        mockPaymentFeeMethod(paymentFeeMethod);

        boolean result = service.isZeroNonChangeableFeeConfigured(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION,
                FeeType.FIXED
        );

        assertThat(result).isTrue();
    }

    @Test
    void isZeroNonChangeableFeeConfigured_shouldReturnTrue_whenWasteFeeIsZeroAndNonChangeable() {
        PaymentFeeMethod paymentFeeMethod = buildPaymentFeeMethod(
                Map.of(
                        FeeType.WASTE,
                        new PaymentFee(
                                BigDecimal.ZERO,
                                false,
                                null,
                                null
                        )
                )
        );

        mockPaymentFeeMethod(paymentFeeMethod);

        boolean result = service.isZeroNonChangeableFeeConfigured(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION,
                FeeType.WASTE
        );

        assertThat(result).isTrue();
    }

    @Test
    void isZeroNonChangeableFeeConfigured_shouldCheckOnlyRequestedFeeType() {
        PaymentFeeMethod paymentFeeMethod = buildPaymentFeeMethod(
                Map.of(
                        FeeType.FIXED,
                        new PaymentFee(
                                BigDecimal.valueOf(100),
                                true,
                                null,
                                null
                        ),
                        FeeType.WASTE,
                        new PaymentFee(
                                BigDecimal.ZERO,
                                false,
                                null,
                                null
                        )
                )
        );

        mockPaymentFeeMethod(paymentFeeMethod);

        boolean fixedResult = service.isZeroNonChangeableFeeConfigured(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION,
                FeeType.FIXED
        );

        boolean wasteResult = service.isZeroNonChangeableFeeConfigured(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION,
                FeeType.WASTE
        );

        assertThat(fixedResult).isFalse();
        assertThat(wasteResult).isTrue();
    }

    @Test
    void isZeroNonChangeableFeeConfigured_shouldReturnFalse_whenFeeAmountIsNotZero() {
        PaymentFeeMethod paymentFeeMethod = buildPaymentFeeMethod(
                Map.of(
                        FeeType.FIXED,
                        new PaymentFee(
                                BigDecimal.valueOf(100),
                                false,
                                null,
                                null
                        )
                )
        );

        mockPaymentFeeMethod(paymentFeeMethod);

        boolean result = service.isZeroNonChangeableFeeConfigured(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION,
                FeeType.FIXED
        );

        assertThat(result).isFalse();
    }

    @Test
    void isZeroNonChangeableFeeConfigured_shouldReturnFalse_whenFeeIsChangeable() {
        PaymentFeeMethod paymentFeeMethod = buildPaymentFeeMethod(
                Map.of(
                        FeeType.FIXED,
                        new PaymentFee(
                                BigDecimal.ZERO,
                                true,
                                null,
                                null
                        )
                )
        );

        mockPaymentFeeMethod(paymentFeeMethod);

        boolean result = service.isZeroNonChangeableFeeConfigured(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION,
                FeeType.FIXED
        );

        assertThat(result).isFalse();
    }

    @Test
    void isZeroNonChangeableFeeConfigured_shouldReturnFalse_whenRequestedFeeTypeDoesNotExist() {
        PaymentFeeMethod paymentFeeMethod = buildPaymentFeeMethod(
                Map.of(
                        FeeType.FIXED,
                        new PaymentFee(
                                BigDecimal.ZERO,
                                false,
                                null,
                                null
                        )
                )
        );

        mockPaymentFeeMethod(paymentFeeMethod);

        boolean result = service.isZeroNonChangeableFeeConfigured(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION,
                FeeType.WASTE
        );

        assertThat(result).isFalse();
    }

    @Test
    void isZeroNonChangeableFeeConfigured_shouldReturnFalse_whenFeeMethodDoesNotExist() {
        when(paymentFeeMethodRepository
                .findByCompetentAuthorityAndRequestType(
                        CompetentAuthorityEnum.WALES,
                        RequestType.PERMIT_VARIATION
                ))
                .thenReturn(Optional.empty());

        boolean result = service.isZeroNonChangeableFeeConfigured(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION,
                FeeType.FIXED
        );

        assertThat(result).isFalse();
    }

    private PaymentFeeMethod buildPaymentFeeMethod(
            Map<FeeType, PaymentFee> fees) {

        return PaymentFeeMethod.builder()
                .competentAuthority(CompetentAuthorityEnum.WALES)
                .requestType(RequestType.PERMIT_VARIATION)
                .type(FeeMethodType.STANDARD)
                .fees(fees)
                .build();
    }

    private void mockPaymentFeeMethod(
            PaymentFeeMethod paymentFeeMethod) {

        when(paymentFeeMethodRepository
                .findByCompetentAuthorityAndRequestType(
                        CompetentAuthorityEnum.WALES,
                        RequestType.PERMIT_VARIATION
                ))
                .thenReturn(Optional.of(paymentFeeMethod));
    }
}