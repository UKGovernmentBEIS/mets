package uk.gov.pmrv.api.competentauthority.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.competentauthority.domain.CompetentAuthority;
import uk.gov.pmrv.api.competentauthority.repository.CompetentAuthorityCustomRepositoryImpl;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
public class CompetentAuthorityCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private CompetentAuthorityCustomRepositoryImpl cut;

    @Autowired
    private EntityManager em;

    @Test
    void findByIdForUpdate() {
    	Optional<CompetentAuthority> result = cut.findByIdForUpdate(CompetentAuthorityEnum.ENGLAND);
    	assertThat(result).isEmpty();
    	
    	CompetentAuthority ca = CompetentAuthority.builder().id(CompetentAuthorityEnum.ENGLAND).build();
    	em.persist(ca);
    	
    	em.flush();
    	em.clear();
    	
    	result = cut.findByIdForUpdate(CompetentAuthorityEnum.ENGLAND);
    	assertThat(result).isNotEmpty();
    	assertThat(result.get().getId()).isEqualTo(CompetentAuthorityEnum.ENGLAND);
    }

}
