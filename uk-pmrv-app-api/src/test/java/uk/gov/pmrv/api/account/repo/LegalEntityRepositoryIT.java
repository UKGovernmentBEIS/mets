package uk.gov.pmrv.api.account.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.domain.HoldingCompany;
import uk.gov.pmrv.api.account.domain.HoldingCompanyAddress;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.pmrv.api.common.domain.Address;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class LegalEntityRepositoryIT extends AbstractContainerBaseTest {
	
	@Autowired
	private LegalEntityRepository repo;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	void findActive() {
		final String leActiveName = "leActive";
		final String lePendingName = "lePending";
		createLegalEntity(leActiveName, LegalEntityStatus.ACTIVE);
		createLegalEntity(lePendingName, LegalEntityStatus.PENDING);
		
		//invoke
		List<LegalEntity> results = repo.findAllByStatusOrderByName(LegalEntityStatus.ACTIVE);
		
		flushAndClear();
		
		//assert
		assertThat(results).hasSize(1);
		assertThat(results.get(0).getName()).isEqualTo(leActiveName);
	}

	@Test
	void findByNameAndStatus() {
		final String leActiveName = "leActive";
		createLegalEntity(leActiveName, LegalEntityStatus.ACTIVE);

		//invoke
		Optional<LegalEntity> result = repo.findByNameAndStatus(leActiveName, LegalEntityStatus.ACTIVE);

		flushAndClear();

		//assert
		assertTrue(result.isPresent());
		assertThat(result.get().getName()).isEqualTo(leActiveName);
	}

	@Test
	void findById() {
		final String leActiveName = "leActive";
		LegalEntity legalEntity = createLegalEntity(leActiveName, LegalEntityStatus.ACTIVE);

		//invoke
		Optional<LegalEntity> result = repo.findById(legalEntity.getId());

		flushAndClear();

		//assert
		assertTrue(result.isPresent());
		assertThat(result.get().getName()).isEqualTo(leActiveName);
	}
	
	@Test
	void existsByNameAndStatus_false() {
		final String leName = "le1";

		createLegalEntity(leName, LegalEntityStatus.PENDING);

		flushAndClear();
		
		assertThat(repo.existsByNameAndStatus(leName, LegalEntityStatus.ACTIVE)).isFalse();
	}
	
	@Test
	void existsByNameAndStatus_true() {
		final String leName = "le1";

		createLegalEntity(leName, LegalEntityStatus.ACTIVE);
		
		flushAndClear();
		
		assertThat(repo.existsByNameAndStatus(leName, LegalEntityStatus.ACTIVE)).isTrue();
	}

    @Test
    void existsByNameAndStatusAndIdNot_false() {
	    String leName1 = "leName1";
	    String leName2 = "leName2";

        LegalEntity le1 = createLegalEntity(leName1, LegalEntityStatus.ACTIVE);
        createLegalEntity(leName2, LegalEntityStatus.ACTIVE);

        assertThat(repo.existsByNameAndStatusAndIdNot(leName1, LegalEntityStatus.ACTIVE, le1.getId())).isFalse();
    }

    @Test
    void existsByNameAndStatusAndIdNot_false2() {
        String leName1 = "leName1";
        String leName2 = "leName2";

        LegalEntity le1 = createLegalEntity(leName1, LegalEntityStatus.ACTIVE);
        createLegalEntity(leName2, LegalEntityStatus.PENDING);

        assertThat(repo.existsByNameAndStatusAndIdNot(leName2, LegalEntityStatus.ACTIVE, le1.getId())).isFalse();
    }

    @Test
    void existsByNameAndStatusAndIdNot_true() {
        String leName1 = "leName1";
        String leName2 = "leName2";

        LegalEntity le1 = createLegalEntity(leName1, LegalEntityStatus.ACTIVE);
        createLegalEntity(leName2, LegalEntityStatus.ACTIVE);

        assertThat(repo.existsByNameAndStatusAndIdNot(leName2, LegalEntityStatus.ACTIVE, le1.getId())).isTrue();
    }

	private LegalEntity createLegalEntity(String name, LegalEntityStatus status) {
		LegalEntity le = LegalEntity.builder()
				.location(
						LocationOnShore.builder()
							.gridReference("grid")
							.address(
									Address.builder()
										.city("city")
										.country("GR")
										.line1("line")
										.postcode("postcode")
										.build())
							.build())
				.name(name)
				.status(status)
				.referenceNumber("regNumber")
				.type(LegalEntityType.LIMITED_COMPANY)
				.holdingCompany(createHoldingCompany())
				.build();
		entityManager.persist(le);
		return le;
	}
	
	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}

	private HoldingCompany createHoldingCompany() {
		return HoldingCompany.builder()
			.name("holding")
			.registrationNumber("123456")
			.address(HoldingCompanyAddress.builder()
				.line1("line1")
				.line2("line2")
				.city("city")
				.postcode("postcode")
				.build())
			.build();
	}
}
