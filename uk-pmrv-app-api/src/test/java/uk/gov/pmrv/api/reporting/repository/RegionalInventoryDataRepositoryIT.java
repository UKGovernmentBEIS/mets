package uk.gov.pmrv.api.reporting.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.reporting.domain.ChargingZone;
import uk.gov.pmrv.api.reporting.domain.EmissionCalculationParams;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.RegionalInventoryData;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class RegionalInventoryDataRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private RegionalInventoryDataRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByReportingYearAndChargingZoneCode_same_zone_different_year() {
        String chargingZoneCode = "EA";
        Year year2021 = Year.of(2021);
        Year year2022 = Year.of(2022);
        ChargingZone chargingZoneA = createChargingZone(chargingZoneCode, "Eastern");
        RegionalInventoryData regionalInventoryData2021 = createRegionalData(chargingZoneA, year2021);
        createRegionalData(chargingZoneA, year2022);

        Optional<RegionalInventoryData> regionalDataOptional = repo.findByReportingYearAndChargingZoneCode(year2021, chargingZoneCode);

        flushAndClear();

        assertThat(regionalDataOptional).isNotEmpty();
        assertEquals(regionalInventoryData2021, regionalDataOptional.get());
    }

    @Test
    void findByReportingYearAndChargingZoneCode2_different_zone_same_year() {
        String chargingZoneCodeA = "EA";
        String chargingZoneCodeB = "EM";
        Year year = Year.of(2022);
        ChargingZone chargingZoneA = createChargingZone(chargingZoneCodeA, "Eastern");
        ChargingZone chargingZoneB = createChargingZone(chargingZoneCodeB, "East Midlands");
        RegionalInventoryData regionalInventoryDataZoneA = createRegionalData(chargingZoneA, year);
        createRegionalData(chargingZoneB, year);

        Optional<RegionalInventoryData> regionalDataOptional = repo.findByReportingYearAndChargingZoneCode(year, chargingZoneCodeA);

        flushAndClear();

        assertThat(regionalDataOptional).isNotEmpty();
        assertEquals(regionalInventoryDataZoneA, regionalDataOptional.get());
    }

    @Test
    void findByReportingYearAndChargingZoneCode_return_empty() {
        String chargingZoneCode = "EA";
        Year year2021 = Year.of(2021);
        Year year2022 = Year.of(2022);
        ChargingZone chargingZoneA = createChargingZone(chargingZoneCode, "Eastern");
        createRegionalData(chargingZoneA, year2021);

        Optional<RegionalInventoryData> regionalDataOptional = repo.findByReportingYearAndChargingZoneCode(year2022, chargingZoneCode);

        flushAndClear();

        assertThat(regionalDataOptional).isEmpty();
    }

    private ChargingZone createChargingZone(String code, String name) {
        ChargingZone chargingZone = ChargingZone.builder().code(code).name(name).build();
        entityManager.persist(chargingZone);
        return chargingZone;
    }

    private RegionalInventoryData createRegionalData(ChargingZone chargingZone, Year reportingYear) {
        RegionalInventoryData regionalInventoryData = RegionalInventoryData.builder()
            .reportingYear(reportingYear)
            .chargingZone(chargingZone)
            .calculationFactor(BigDecimal.valueOf(Math.random()))
            .emissionCalculationParams(EmissionCalculationParams.builder()
                .emissionFactor(BigDecimal.valueOf(Math.random()))
                .netCalorificValue(BigDecimal.valueOf(Math.random()))
                .oxidationFactor(BigDecimal.valueOf(Math.random()))
                .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
                .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ)
                .build())
            .build();
        entityManager.persist(regionalInventoryData);
        return regionalInventoryData;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}