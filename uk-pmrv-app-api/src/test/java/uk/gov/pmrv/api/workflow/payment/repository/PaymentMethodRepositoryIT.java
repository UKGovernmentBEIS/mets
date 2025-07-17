package uk.gov.pmrv.api.workflow.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class PaymentMethodRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private PaymentMethodRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByCompetentAuthority() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        PaymentMethod creditOrDebitCardPaymentMethodEngland = PaymentMethod.builder()
            .competentAuthority(competentAuthority)
            .type(PaymentMethodType.CREDIT_OR_DEBIT_CARD)
            .build();
        PaymentMethod bankTransferPaymentMethodEngland = PaymentMethod.builder()
            .competentAuthority(competentAuthority)
            .type(PaymentMethodType.BANK_TRANSFER)
            .build();
        PaymentMethod bankTransferPaymentMethodWales = PaymentMethod.builder()
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .type(PaymentMethodType.BANK_TRANSFER)
            .build();

        entityManager.persist(creditOrDebitCardPaymentMethodEngland);
        entityManager.persist(bankTransferPaymentMethodEngland);
        entityManager.persist(bankTransferPaymentMethodWales);

        flushAndClear();

        List<PaymentMethod> paymentMethods =
            repository.findByCompetentAuthority(competentAuthority);

        assertThat(paymentMethods).isNotEmpty();
        assertThat(paymentMethods).containsExactlyInAnyOrder(creditOrDebitCardPaymentMethodEngland,bankTransferPaymentMethodEngland);
    }

    @Test
    void findByCompetentAuthority_no_result() {
        PaymentMethod paymentMethod = PaymentMethod.builder()
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .type(PaymentMethodType.CREDIT_OR_DEBIT_CARD)
            .build();

        entityManager.persist(paymentMethod);

        flushAndClear();

        List<PaymentMethod> optionalResult =
            repository.findByCompetentAuthority(CompetentAuthorityEnum.WALES);

        assertThat(optionalResult).isEmpty();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}