package uk.gov.pmrv.api.referencedata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.referencedata.domain.Country;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class CountryRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private CountryRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByName() {
        final Country country = Country.builder()
                .code("GR")
                .name("Greece")
                .officialName("Greece_official")
                .build();

        entityManager.persist(country);
        flushAndClear();

        final Optional<Country> actual = repository.findByName("Greece");
        assertTrue(actual.isPresent());
        assertEquals("GR", actual.get().getCode());
        assertEquals("Greece", actual.get().getName());
        assertEquals("Greece_official", actual.get().getOfficialName());
    }
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
