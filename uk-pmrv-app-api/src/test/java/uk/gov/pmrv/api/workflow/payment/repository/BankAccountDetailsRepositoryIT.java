package uk.gov.pmrv.api.workflow.payment.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.domain.BankAccountDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class BankAccountDetailsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private BankAccountDetailsRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByCompetentAuthority() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        BankAccountDetails expectedBankAccountDetails = BankAccountDetails.builder()
            .competentAuthority(competentAuthority)
            .accountName("accountName")
            .sortCode("sortCode")
            .accountNumber("accountNumber")
            .iban("iban")
            .swiftCode("swiftCode")
            .build();

        entityManager.persist(expectedBankAccountDetails);

        flushAndClear();

        Optional<BankAccountDetails> result = repository.findByCompetentAuthority(competentAuthority);

        assertThat(result).isNotEmpty();
        assertEquals(expectedBankAccountDetails, result.get());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}