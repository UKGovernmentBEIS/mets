package uk.gov.pmrv.api.competentauthority.repository;

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
import uk.gov.pmrv.api.competentauthority.domain.CompetentAuthority;
import uk.gov.pmrv.api.competentauthority.repository.CompetentAuthorityRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class})
public class CompetentAuthorityRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    CompetentAuthorityRepository competentAuthorityRepository;

    @Autowired
    EntityManager em;

    @Test
    void findById() {
    	CompetentAuthority ca = CompetentAuthority.builder().id(CompetentAuthorityEnum.ENGLAND).build();
    	em.persist(ca);
    	
    	em.flush();
    	em.clear();

		CompetentAuthority result = competentAuthorityRepository.findById(CompetentAuthorityEnum.ENGLAND);
    	assertEquals(result.getId(), CompetentAuthorityEnum.ENGLAND);
    }
}