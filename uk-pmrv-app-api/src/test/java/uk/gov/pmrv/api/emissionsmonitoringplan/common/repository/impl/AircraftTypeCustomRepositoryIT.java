package uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.AircraftType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.AircraftTypeId;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchCriteria;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchResults;

import jakarta.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AircraftTypeCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AircraftTypeCustomRepositoryImpl repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findBySearchCriteria_nor_term_nor_exclusions() {
        createAircraftType("BOEING", "747", "B747");
        createAircraftType("LEARJET", "35", "LJ35");

        AircraftTypeSearchCriteria searchCriteria = AircraftTypeSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build())
            .build();

        AircraftTypeDTO aircraftTypeDTOBoeing = new AircraftTypeDTO("BOEING", "747", "B747");
        AircraftTypeDTO aircraftTypeDTOLearjet = new AircraftTypeDTO("LEARJET", "35", "LJ35");

        flushAndClear();

        AircraftTypeSearchResults searchResults = repo.findBySearchCriteria(searchCriteria);

        assertEquals(2L, searchResults.getTotal());
        assertThat(searchResults.getAircraftTypes()).containsExactly(aircraftTypeDTOBoeing, aircraftTypeDTOLearjet);
    }

    @Test
    void findBySearchCriteria_with_term_only() {
        createAircraftType("BOEING", "747", "AIR747");
        createAircraftType("SUPER PUMA", "Ultra Air", "AB12");
        createAircraftType("LEARAIR", "35", "LJ35");
        createAircraftType("OSPREY", "Opsrey 2", "MD40");

        AircraftTypeSearchCriteria searchCriteria = AircraftTypeSearchCriteria.builder()
            .term("air")
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build())
            .build();

        AircraftTypeDTO aircraftTypeDTOBoeing = new AircraftTypeDTO("BOEING", "747", "AIR747");
        AircraftTypeDTO aircraftTypeDTOSuperPuma = new AircraftTypeDTO("SUPER PUMA", "Ultra Air", "AB12");
        AircraftTypeDTO aircraftTypeDTOLearair = new AircraftTypeDTO("LEARAIR", "35", "LJ35");

        flushAndClear();

        AircraftTypeSearchResults searchResults = repo.findBySearchCriteria(searchCriteria);

        assertEquals(3L, searchResults.getTotal());
        assertThat(searchResults.getAircraftTypes()).containsExactly(aircraftTypeDTOBoeing, aircraftTypeDTOLearair, aircraftTypeDTOSuperPuma);
    }

    @Test
    void findBySearchCriteria_with_exclusions_only() {
        createAircraftType("BOEING", "747", "B747");
        createAircraftType("BOEING", "746", "B746");
        createAircraftType("SUPER PUMA", "Ultra Air", "AB12");

        AircraftTypeSearchCriteria searchCriteria = AircraftTypeSearchCriteria.builder()
            .excludedAircraftTypes(List.of(new AircraftTypeDTO("BOEING", "746", "B746")))
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build())
            .build();

        AircraftTypeDTO aircraftTypeDTOBoeing = new AircraftTypeDTO("BOEING", "747", "B747");
        AircraftTypeDTO aircraftTypeDTOSuperPuma = new AircraftTypeDTO("SUPER PUMA", "Ultra Air", "AB12");

        flushAndClear();

        AircraftTypeSearchResults searchResults = repo.findBySearchCriteria(searchCriteria);

        assertEquals(2L, searchResults.getTotal());
        assertThat(searchResults.getAircraftTypes()).containsExactly(aircraftTypeDTOBoeing, aircraftTypeDTOSuperPuma);
    }

    @Test
    void findBySearchCriteria_with_term_amd_exclusions() {
        createAircraftType("BOEING", "747", "AIR747");
        createAircraftType("SUPER PUMA", "Ultra Air", "AB12");
        createAircraftType("LEARAIR", "35", "LJ35");
        createAircraftType("OSPREY", "Opsrey 2", "MD40");

        AircraftTypeSearchCriteria searchCriteria = AircraftTypeSearchCriteria.builder()
            .term("air")
            .excludedAircraftTypes(List.of(new AircraftTypeDTO("SUPER PUMA", "Ultra Air", "AB12")))
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build())
            .build();

        AircraftTypeDTO aircraftTypeDTOBoeing = new AircraftTypeDTO("BOEING", "747", "AIR747");
        AircraftTypeDTO aircraftTypeDTOLearair = new AircraftTypeDTO("LEARAIR", "35", "LJ35");

        flushAndClear();

        AircraftTypeSearchResults searchResults = repo.findBySearchCriteria(searchCriteria);

        assertEquals(2L, searchResults.getTotal());
        assertThat(searchResults.getAircraftTypes()).containsExactly(aircraftTypeDTOBoeing, aircraftTypeDTOLearair);
    }

    @Test
    void findBySearchCriteria_no_results_found() {
        createAircraftType("BOEING", "747", "B747");
        createAircraftType("SUPER PUMA", "Ultra", "AB12");

        AircraftTypeSearchCriteria searchCriteria = AircraftTypeSearchCriteria.builder()
            .term("air")
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build())
            .build();

        flushAndClear();

        AircraftTypeSearchResults searchResults = repo.findBySearchCriteria(searchCriteria);

        assertEquals(0L, searchResults.getTotal());
        assertThat(searchResults.getAircraftTypes()).isEmpty();
    }

    @Test
    void findInvalidDesignatorCodes_no_results_found() {
        createAircraftType("BOEING", "747", "B747");
        createAircraftType("SUPER PUMA", "Ultra", "AB12");
        List<String> designatorCodes = List.of("B747","AB12");

        List<String> validDesignatorCodes = repo.findInvalidDesignatorCodes(designatorCodes);

        flushAndClear();

        assertTrue(validDesignatorCodes.isEmpty());
    }

    private void createAircraftType(String manufacturer, String model, String designatorType) {
        AircraftType aircraftType = AircraftType.builder()
                .aircraftTypeId(new AircraftTypeId(manufacturer, model, designatorType))
            .build();

        entityManager.persist(aircraftType);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}