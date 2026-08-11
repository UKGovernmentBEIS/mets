package uk.gov.pmrv.api.settings.repository;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.settings.domain.dto.FeeRowDTO;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFee;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
class SettingsFeeRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private SettingsFeeRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findChangeableFeesByCompetentAuthority_returnsOnlyChangeableFees() {
        persist(CompetentAuthorityEnum.ENGLAND, Map.of(
                FeeType.FIXED, PaymentFee.builder().amount(new BigDecimal("1234")).build(),
                FeeType.HSE, PaymentFee.builder().amount(new BigDecimal("567")).changeable(false).build()
        ));

        flushAndClear();

        List<FeeRowDTO> result = repository.findChangeableFeesByCompetentAuthority(CompetentAuthorityEnum.ENGLAND);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getRequestType()).isEqualTo(RequestType.PERMIT_SURRENDER);
        assertThat(result.getFirst().getFeeType()).isEqualTo(FeeType.FIXED);
        assertThat(result.getFirst().getAmount()).isEqualByComparingTo("1234");
    }

    @Test
    void findChangeableFeesByCompetentAuthority_returnsEmpty_whenAllFeesUnchangeable() {
        persist(CompetentAuthorityEnum.ENGLAND, Map.of(
                FeeType.FIXED, PaymentFee.builder().amount(new BigDecimal("1234")).changeable(false).build()
        ));

        flushAndClear();

        List<FeeRowDTO> result = repository.findChangeableFeesByCompetentAuthority(CompetentAuthorityEnum.ENGLAND);

        assertThat(result).isEmpty();
    }

    @Test
    void findChangeableFeesByCompetentAuthority_doesNotReturnOtherCaFees() {
        persist(CompetentAuthorityEnum.ENGLAND, Map.of(
                FeeType.FIXED, PaymentFee.builder().amount(new BigDecimal("1234")).build()
        ));
        persist(CompetentAuthorityEnum.SCOTLAND, Map.of(
                FeeType.FIXED, PaymentFee.builder().amount(new BigDecimal("999")).build()
        ));

        flushAndClear();

        List<FeeRowDTO> result = repository.findChangeableFeesByCompetentAuthority(CompetentAuthorityEnum.ENGLAND);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getRequestType()).isEqualTo(RequestType.PERMIT_SURRENDER);
    }

    @Test
    void findChangeableFeesByCompetentAuthority_returnsScheduledChangeFields() {
        LocalDate scheduledDate = LocalDate.of(2026, 9, 1);
        persist(CompetentAuthorityEnum.ENGLAND, Map.of(
                FeeType.FIXED, PaymentFee.builder()
                        .amount(new BigDecimal("1234"))
                        .scheduledAmount(new BigDecimal("1500"))
                        .scheduledDate(scheduledDate)
                        .build()
        ));

        flushAndClear();

        List<FeeRowDTO> result = repository.findChangeableFeesByCompetentAuthority(CompetentAuthorityEnum.ENGLAND);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getScheduledAmount()).isEqualByComparingTo("1500");
        assertThat(result.getFirst().getScheduledDate()).isEqualTo(scheduledDate);
    }

    @Test
    void findChangeableFeesByCompetentAuthority_returnsNullScheduledFields_whenNoneSet() {
        persist(CompetentAuthorityEnum.ENGLAND, Map.of(
                FeeType.FIXED, PaymentFee.builder().amount(new BigDecimal("1234")).build()
        ));

        flushAndClear();

        List<FeeRowDTO> result = repository.findChangeableFeesByCompetentAuthority(CompetentAuthorityEnum.ENGLAND);

        assertThat(result.getFirst().getScheduledAmount()).isNull();
        assertThat(result.getFirst().getScheduledDate()).isNull();
    }

    private void persist(CompetentAuthorityEnum ca, Map<FeeType, PaymentFee> fees) {
        EnumMap<FeeType, PaymentFee> feeMap = new EnumMap<>(FeeType.class);
        feeMap.putAll(fees);
        entityManager.persist(PaymentFeeMethod.builder()
                .competentAuthority(ca)
                .requestType(RequestType.PERMIT_SURRENDER)
                .type(FeeMethodType.STANDARD)
                .fees(feeMap)
                .build());
        entityManager.flush();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
