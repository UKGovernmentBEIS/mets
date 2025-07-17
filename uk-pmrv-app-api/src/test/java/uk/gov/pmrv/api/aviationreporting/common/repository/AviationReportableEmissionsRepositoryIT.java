package uk.gov.pmrv.api.aviationreporting.common.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AviationReportableEmissionsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AviationReportableEmissionsRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllByAccountIdAndYearIn() {
        Long accountId = 1L;
        AviationReportableEmissionsEntity reportableEmissionsEntity2021 =
            createReportablaEmissionsEntry(accountId, Year.of(2021), BigDecimal.valueOf(1020.458));
            AviationReportableEmissionsEntity reportableEmissionsEntity2022 =
            createReportablaEmissionsEntry(accountId, Year.of(2022), BigDecimal.valueOf(1080.29));
        AviationReportableEmissionsEntity reportableEmissionsEntity2023 =
            createReportablaEmissionsEntry(accountId, Year.of(2023), BigDecimal.valueOf(1291.90));

        flushAndClear();

        List<AviationReportableEmissionsEntity> reportableEmissionsResult = repository.findAllByAccountIdAndYearIn(accountId, Set.of(Year.of(2022), Year.of(2021)));

        assertThat(reportableEmissionsResult).hasSize(2);
        assertThat(reportableEmissionsResult).containsExactlyInAnyOrder(reportableEmissionsEntity2021, reportableEmissionsEntity2022);
    }

    @Test
    void findByAccountIdAndYear() {
        Long accountId = 1L;
        AviationReportableEmissionsEntity reportableEmissionsEntity =
            createReportablaEmissionsEntry(accountId, Year.of(2021), BigDecimal.valueOf(1020.458));
        createReportablaEmissionsEntry(accountId, Year.of(2022), BigDecimal.valueOf(1080.29));
        createReportablaEmissionsEntry(accountId, Year.of(2023), BigDecimal.valueOf(1291.90));

        flushAndClear();

        Optional<AviationReportableEmissionsEntity> resultOptional =
            repository.findByAccountIdAndYear(accountId, Year.of(2021));

        assertThat(resultOptional).isNotEmpty();
        assertEquals(reportableEmissionsEntity, resultOptional.get());
    }

    @Test
    void findByAccountIdAndYear_empty() {
        Long accountId = 1L;
        createReportablaEmissionsEntry(accountId, Year.of(2021), BigDecimal.valueOf(1020.458));

        flushAndClear();

        Optional<AviationReportableEmissionsEntity> resultOptional =
            repository.findByAccountIdAndYear(accountId, Year.of(2022));

        assertThat(resultOptional).isEmpty();
    }

    private AviationReportableEmissionsEntity createReportablaEmissionsEntry(Long accountId, Year year, BigDecimal reportableEmissions) {
        AviationReportableEmissionsEntity reportableEmissionsEntity = AviationReportableEmissionsEntity.builder()
            .accountId(accountId)
            .year(year)
            .reportableEmissions(reportableEmissions)
            .build();

        entityManager.persist(reportableEmissionsEntity);
        return reportableEmissionsEntity;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}