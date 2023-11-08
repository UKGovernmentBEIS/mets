package uk.gov.pmrv.api.emissionsmonitoringplan.common.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpIssuingAuthority;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmpIssuingAuthorityRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class EmpIssuingAuthorityRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private EmpIssuingAuthorityRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllIssuingAuthorityNames() {

        String name1 = "name1";
        String name2 = "name2";
        final EmpIssuingAuthority empIssuingAuthority1 = EmpIssuingAuthority.builder()
                .name(name1)
                .build();

        final EmpIssuingAuthority empIssuingAuthority2 = EmpIssuingAuthority.builder()
                .name(name2)
                .build();

        entityManager.persist(empIssuingAuthority1);
        entityManager.persist(empIssuingAuthority2);

        flushAndClear();

        final List<String> issuingAuthorityNames = repository.findAllIssuingAuthorityNames();

        assertThat(issuingAuthorityNames).containsOnly(name1, name2);
    }

    @Test
    void findAllIssuingAuthorityNames_no_results() {

        final List<String> issuingAuthorityNames = repository.findAllIssuingAuthorityNames();

        assertThat(issuingAuthorityNames).isEmpty();
    }

    @Test
    void existByName() {

        String name = "name";
        final EmpIssuingAuthority empIssuingAuthority = EmpIssuingAuthority.builder()
                .name(name)
                .build();

        entityManager.persist(empIssuingAuthority);

        flushAndClear();

        final boolean actual = repository.existsByName(name);

        assertTrue(actual);
    }

    @Test
    void existByName_should_return_false() {

        String name = "name";

        final boolean actual = repository.existsByName(name);

        assertFalse(actual);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
