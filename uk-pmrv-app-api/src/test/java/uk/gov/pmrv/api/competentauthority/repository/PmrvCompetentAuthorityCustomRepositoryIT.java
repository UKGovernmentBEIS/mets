package uk.gov.pmrv.api.competentauthority.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.competentauthority.CompetentAuthorityRepository;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthority;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
public class PmrvCompetentAuthorityCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private CompetentAuthorityRepository<PmrvCompetentAuthority> cut;

    @Autowired
    private EntityManager em;

    @Test
    void findByIdForUpdate() {
    	Optional<PmrvCompetentAuthority> result = cut.findByIdForUpdate(CompetentAuthorityEnum.ENGLAND);
    	assertThat(result).isEmpty();
    	
    	PmrvCompetentAuthority ca = PmrvCompetentAuthority.builder().id(CompetentAuthorityEnum.ENGLAND).build();
    	em.persist(ca);
    	
    	em.flush();
    	em.clear();
    	
    	result = cut.findByIdForUpdate(CompetentAuthorityEnum.ENGLAND);
    	assertThat(result).isNotEmpty();
    	assertThat(result.get().getId()).isEqualTo(CompetentAuthorityEnum.ENGLAND);
    }

}
