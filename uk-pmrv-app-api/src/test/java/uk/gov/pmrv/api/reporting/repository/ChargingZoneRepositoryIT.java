package uk.gov.pmrv.api.reporting.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.reporting.domain.ChargingZone;
import uk.gov.pmrv.api.reporting.domain.PostCode;

import jakarta.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class ChargingZoneRepositoryIT extends AbstractContainerBaseTest {


    @Autowired
    private ChargingZoneRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByPostCodesCodeIgnoreCase() {
        String code1 = "AA11";
        String code2 = "AA13";
        String code3 = "AA15";
        ChargingZone chargingZoneA = createChargingZone("EA", "Eastern");
        createPostCode(chargingZoneA, code1);
        createPostCode(chargingZoneA, code2);

        ChargingZone chargingZoneB = createChargingZone("SO", "Southern");
        createPostCode(chargingZoneB, code1);

        ChargingZone chargingZoneC = createChargingZone("NO", "Northern");
        createPostCode(chargingZoneC, code3);

        List<ChargingZone> result = repo.findByPostCodesCodeIgnoreCase("Aa11");

        flushAndClear();

        assertThat(result).isNotEmpty();
        assertEquals(2, result.size());
        assertThat(result).extracting(ChargingZone::getCode).containsExactlyInAnyOrder("EA", "SO");
    }

    @Test
    void findByPostCodesCodeIgnoreCase_empty_list() {
        String code1 = "AA11";
        String code2 = "AA13";
        String code3 = "AA15";
        ChargingZone chargingZoneA = createChargingZone("EA", "Eastern");
        createPostCode(chargingZoneA, code1);
        createPostCode(chargingZoneA, code2);

        ChargingZone chargingZoneB = createChargingZone("SO", "Southern");
        createPostCode(chargingZoneB, code1);

        List<ChargingZone> result = repo.findByPostCodesCodeIgnoreCase(code3);

        flushAndClear();

        assertThat(result).isEmpty();
    }

    private ChargingZone createChargingZone(String code, String name) {
        ChargingZone chargingZone = ChargingZone.builder().code(code).name(name).build();
        entityManager.persist(chargingZone);
        return chargingZone;
    }

    private PostCode createPostCode(ChargingZone chargingZone, String code) {
        PostCode postCode = PostCode.builder().chargingZone(chargingZone).code(code).build();
        entityManager.persist(postCode);
        return postCode;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}