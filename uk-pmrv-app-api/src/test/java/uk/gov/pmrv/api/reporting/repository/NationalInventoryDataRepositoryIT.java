package uk.gov.pmrv.api.reporting.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.reporting.domain.EmissionCalculationParams;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.IpccSector;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NationalInventoryData;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class NationalInventoryDataRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private NationalInventoryDataRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByReportingYearOrderBySectorAscFuelAsc() {
        Year year2021 = Year.of(2021);
        Year year2022 = Year.of(2022);
        IpccSector sector1A1a = IpccSector.builder().id(1L).name("1A1a").displayName("1A1adisplay").build();
        entityManager.persist(sector1A1a);

        IpccSector sector1A2a = IpccSector.builder().id(2L).name("1A2a").displayName("1A2adisplay").build();
        entityManager.persist(sector1A2a);

        String fuelCoke = "Coke";
        String fuelGas = "Gas";
        String fuelPetroleum = "Petroleum";
        NationalInventoryData data2021_1A1a_Gas = createNationalInventoryData(year2021, sector1A1a, fuelGas);
        NationalInventoryData data2021_1A2a_Coke = createNationalInventoryData(year2021, sector1A2a, fuelCoke);
        NationalInventoryData data2021_1A1a_Petroleum = createNationalInventoryData(year2021, sector1A1a, fuelPetroleum);
        NationalInventoryData data2021_1A2a_Petroleum = createNationalInventoryData(year2021, sector1A2a, fuelPetroleum);
        NationalInventoryData data2022_1A2a_Coke = createNationalInventoryData(year2022, sector1A2a, fuelCoke);

        List<NationalInventoryData> result = repo.findByReportingYearOrderBySectorAscFuelAsc(year2021);
        flushAndClear();

        assertThat(result).isNotEmpty();
        assertEquals(4, result.size());
        assertThat(result).containsExactly(data2021_1A1a_Gas, data2021_1A1a_Petroleum, data2021_1A2a_Coke, data2021_1A2a_Petroleum);
    }

    @Test
    void findByReportingYearOrderBySectorAscFuelAsc_empty_list() {
        Year year2021 = Year.of(2021);
        Year year2022 = Year.of(2022);
        IpccSector sector1A1a = IpccSector.builder().id(3L).name("1A1ciii").displayName("1A1ciiidisplay").build();
        entityManager.persist(sector1A1a);

        createNationalInventoryData(year2021, sector1A1a, "Gas");

        List<NationalInventoryData> result = repo.findByReportingYearOrderBySectorAscFuelAsc(year2022);
        flushAndClear();

        assertThat(result).isEmpty();
    }

    @Test
    void findByReportingYearAndSectorAndFuel() {
        Year year = Year.of(2022);
        IpccSector sector = IpccSector.builder().id(4L).name("1B1b").displayName("1B1bdisplay").build();
        entityManager.persist(sector);

        String fuel = "Gas";

        NationalInventoryData data = createNationalInventoryData(year, sector, fuel);
        createNationalInventoryData(year, sector, "Coke");

        Optional<NationalInventoryData> result = repo.findByReportingYearAndSectorAndFuel(year, sector.getName(), fuel);
        flushAndClear();

        assertThat(result).isNotEmpty();
        assertEquals(data, result.get());
    }

    private NationalInventoryData createNationalInventoryData(Year reportingYear, IpccSector sector, String fuel) {
        NationalInventoryData regionalData = NationalInventoryData.builder()
            .reportingYear(reportingYear)
            .sector(sector)
            .fuel(fuel)
            .emissionCalculationParams(EmissionCalculationParams.builder()
                .emissionFactor(BigDecimal.valueOf(Math.random()))
                .netCalorificValue(BigDecimal.valueOf(Math.random()))
                .oxidationFactor(BigDecimal.valueOf(Math.random()))
                .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
                .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ)
                .build())
            .build();
        entityManager.persist(regionalData);
        return regionalData;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}