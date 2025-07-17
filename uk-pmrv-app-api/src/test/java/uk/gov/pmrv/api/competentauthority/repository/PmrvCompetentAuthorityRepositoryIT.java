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
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthorityRepository;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthority;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class})
public class PmrvCompetentAuthorityRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private PmrvCompetentAuthorityRepository competentAuthorityRepository;

    @Autowired
    private EntityManager em;

    @Test
    void findById() {
    	PmrvCompetentAuthority ca = PmrvCompetentAuthority.builder().id(CompetentAuthorityEnum.ENGLAND).build();
    	em.persist(ca);
    	
    	em.flush();
    	em.clear();

		Optional<PmrvCompetentAuthority> resultOpt = competentAuthorityRepository.findById(CompetentAuthorityEnum.ENGLAND);
    	assertThat(resultOpt.get().getId()).isEqualTo(CompetentAuthorityEnum.ENGLAND);
    	
    	resultOpt = competentAuthorityRepository.findById(CompetentAuthorityEnum.NORTHERN_IRELAND);
    	assertThat(resultOpt).isEmpty();
    }
}