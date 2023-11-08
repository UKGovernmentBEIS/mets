package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionType;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TransferredCO2AndN2OMonitoringApproach;
import uk.gov.pmrv.api.reporting.domain.Aer;

class AerEmissionPointsInitializationServiceTest {
    
    private final AerEmissionPointsInitializationService service = new AerEmissionPointsInitializationService();

    @Test
    void initialize() {
        String epId1 = UUID.randomUUID().toString();
        String epId2 = UUID.randomUUID().toString();
        String epId3 = UUID.randomUUID().toString();

        EmissionPoint ep1 = EmissionPoint.builder().id(epId1).reference("ref1").description("desc1").build();
        EmissionPoint ep2 = EmissionPoint.builder().id(epId2).reference("ref2").description("desc2").build();
        EmissionPoint ep3 = EmissionPoint.builder().id(epId3).reference("ref3").description("desc3").build();

        MeasurementOfCO2MonitoringApproach measMonitoringApproach = MeasurementOfCO2MonitoringApproach.builder()
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .emissionPointCategoryAppliedTiers(List.of(MeasurementOfCO2EmissionPointCategoryAppliedTier.builder()
                .emissionPointCategory(MeasurementOfCO2EmissionPointCategory.builder()
                    .emissionPoint(epId1)
                    .sourceStreams(Set.of(UUID.randomUUID().toString()))
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000))
                    .categoryType(CategoryType.MAJOR)
                    .build())
                .build()))
            .build();

        MeasurementOfN2OMonitoringApproach n2oMonitoringApproach = MeasurementOfN2OMonitoringApproach.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointCategoryAppliedTiers(List.of(MeasurementOfN2OEmissionPointCategoryAppliedTier.builder()
                .emissionPointCategory(MeasurementOfN2OEmissionPointCategory.builder()
                    .emissionPoint(epId2)
                    .sourceStreams(Set.of(UUID.randomUUID().toString()))
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .emissionType(MeasurementOfN2OEmissionType.ABATED)
                    .monitoringApproachType(MeasurementOfN2OMonitoringApproachType.CALCULATION)
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(27500))
                    .categoryType(CategoryType.MAJOR)
                    .build())
                .build()))
            .build();

        CalculationOfPFCMonitoringApproach pfcMonitoringApproach = CalculationOfPFCMonitoringApproach.builder()
            .type(MonitoringApproachType.CALCULATION_PFC)
            .sourceStreamCategoryAppliedTiers(List.of(PFCSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(PFCSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .emissionPoints(Set.of(epId3))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(25000))
                    .categoryType(CategoryType.MAJOR)
                    .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
                    .build())
                .build()))
            .build();

        EnumMap<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            new EnumMap<>(MonitoringApproachType.class);
        monitoringApproaches.put(MonitoringApproachType.MEASUREMENT_CO2, measMonitoringApproach);
        monitoringApproaches.put(MonitoringApproachType.MEASUREMENT_N2O, n2oMonitoringApproach);
        monitoringApproaches.put(MonitoringApproachType.CALCULATION_PFC, pfcMonitoringApproach);

        Aer aer = Aer.builder().build();
        
        Permit permit = Permit.builder()
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(ep1, ep2, ep3)).build())
            .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(monitoringApproaches).build())
            .build();

        service.initialize(aer, permit);

        assertNotNull(aer.getEmissionPoints());
        assertThat(aer.getEmissionPoints().getEmissionPoints()).containsExactlyInAnyOrder(ep1, ep2);
    }

    @Test
    void initialize_with_null() {
        String epId1 = UUID.randomUUID().toString();
        String epId2 = UUID.randomUUID().toString();
        EmissionPoint ep1 = EmissionPoint.builder().id(epId1).reference("ref1").description("desc1").build();
        EmissionPoint ep2 = EmissionPoint.builder().id(epId2).reference("ref2").description("desc2").build();

        TransferredCO2AndN2OMonitoringApproach transferredCO2AndN2OMonitoringApproach = TransferredCO2AndN2OMonitoringApproach.builder()
            .type(MonitoringApproachType.TRANSFERRED_CO2_N2O)
            .build();

        EnumMap<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            new EnumMap<>(MonitoringApproachType.class);
        monitoringApproaches.put(MonitoringApproachType.TRANSFERRED_CO2_N2O, transferredCO2AndN2OMonitoringApproach);

        Aer aer = Aer.builder().build();

        Permit permit = Permit.builder()
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(ep1, ep2)).build())
            .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(monitoringApproaches).build())
            .build();

        service.initialize(aer, permit);

        assertNull(aer.getEmissionPoints());
    }
}