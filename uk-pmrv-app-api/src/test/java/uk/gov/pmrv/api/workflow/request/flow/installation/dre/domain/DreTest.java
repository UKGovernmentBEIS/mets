package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.verification.ReportableAndBiomassEmission;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.CalculationOfCO2ReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.DreMonitoringApproachReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.FallbackReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.MeasurementOfCO2ReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.MeasurementOfN2OReportingEmissions;

public class DreTest {

	private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
	
	@Test
	void determination_other_selected_not_valid() {
		DreDeterminationReason reason = DreDeterminationReason.builder()
				.type(DreDeterminationReasonType.OTHER).build();
		
		Set<ConstraintViolation<DreDeterminationReason>> violations = validator.validate(reason);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{dre.determinationreason.type.typeOtherSummary}");
	}
	
	@Test
	void determination_other_selected_valid() {
		DreDeterminationReason reason = DreDeterminationReason.builder()
				.type(DreDeterminationReasonType.OTHER)
				.typeOtherSummary("other summary")
				.build();
		
		Set<ConstraintViolation<DreDeterminationReason>> violations = validator.validate(reason);
		assertThat(violations).hasSize(0);
	}
	
	@Test
	void fee_false_not_valid() {
		DreFee fee = DreFee.builder()
				.chargeOperator(false)
				.feeDetails(DreFeeDetails.builder()
						.hourlyRate(BigDecimal.valueOf(1L))
						.dueDate(LocalDate.now())
						.totalBillableHours(BigDecimal.valueOf(8L))
						.build())
				.build();
		
		Set<ConstraintViolation<DreFee>> violations = validator.validate(fee);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{dre.chargeoperatorfeedetails}");
	}
	
	@Test
	void fee_true_not_valid() {
		DreFee fee = DreFee.builder()
				.chargeOperator(true)
				.feeDetails(null)
				.build();
		
		Set<ConstraintViolation<DreFee>> violations = validator.validate(fee);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{dre.chargeoperatorfeedetails}");
	}
	
	@Test
	void fee_valid() {
		DreFee fee = DreFee.builder()
				.chargeOperator(true)
				.feeDetails(DreFeeDetails.builder()
						.hourlyRate(BigDecimal.valueOf(1L))
						.dueDate(LocalDate.now())
						.totalBillableHours(BigDecimal.valueOf(8L))
						.build())
				.build();
		
		Set<ConstraintViolation<DreFee>> violations = validator.validate(fee);
		assertThat(violations).hasSize(0);
	}
	
	@Test
	void calculationOfCO2ReportingEmissions_calculateTransferredCO2_true_invalid() {
		CalculationOfCO2ReportingEmissions emissions = CalculationOfCO2ReportingEmissions.builder()
				.type(MonitoringApproachType.CALCULATION_CO2)
				.combustionEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.processEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.massBalanceEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.calculateTransferredCO2(true)
				.build();
		
		Set<ConstraintViolation<CalculationOfCO2ReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{dre.monitoringApproaches.calculationofco2.transferredco2emissions}");
	}
	
	@Test
	void calculationOfCO2ReportingEmissions_calculateTransferredCO2_false_invalid() {
		CalculationOfCO2ReportingEmissions emissions = CalculationOfCO2ReportingEmissions.builder()
				.type(MonitoringApproachType.CALCULATION_CO2)
				.combustionEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.processEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.massBalanceEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.calculateTransferredCO2(false)
				.transferredCO2Emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.build();
		
		Set<ConstraintViolation<CalculationOfCO2ReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{dre.monitoringApproaches.calculationofco2.transferredco2emissions}");
	}
	
	@Test
	void calculationOfCO2ReportingEmissions_calculateTransferredCO2_false_valid() {
		CalculationOfCO2ReportingEmissions emissions = CalculationOfCO2ReportingEmissions.builder()
				.type(MonitoringApproachType.CALCULATION_CO2)
				.combustionEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.processEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.massBalanceEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.calculateTransferredCO2(false)
				.build();
		
		Set<ConstraintViolation<CalculationOfCO2ReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(0);
	}
	
	@Test
	void calculationOfCO2ReportingEmissions_calculateTransferredCO2_true_valid() {
		CalculationOfCO2ReportingEmissions emissions = CalculationOfCO2ReportingEmissions.builder()
				.type(MonitoringApproachType.CALCULATION_CO2)
				.combustionEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.processEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.massBalanceEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.calculateTransferredCO2(true)
				.transferredCO2Emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.build();
		
		Set<ConstraintViolation<CalculationOfCO2ReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(0);
	}
	
	
	
	@Test
	void measurementOfCO2ReportingEmissions_measureTransferredCO2_true_invalid() {
		MeasurementOfCO2ReportingEmissions emissions = MeasurementOfCO2ReportingEmissions.builder()
				.type(MonitoringApproachType.MEASUREMENT_CO2)
				.emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.measureTransferredCO2(true)
				.build();
		
		Set<ConstraintViolation<MeasurementOfCO2ReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{dre.monitoringApproaches.measurementofco2.transferredco2emissions}");
	}
	
	@Test
	void measurementOfCO2ReportingEmissions_measureTransferredCO2_false_invalid() {
		MeasurementOfCO2ReportingEmissions emissions = MeasurementOfCO2ReportingEmissions.builder()
				.type(MonitoringApproachType.MEASUREMENT_CO2)
				.emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.measureTransferredCO2(false)
				.transferredCO2Emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.build();
		
		Set<ConstraintViolation<MeasurementOfCO2ReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{dre.monitoringApproaches.measurementofco2.transferredco2emissions}");
	}
	
	@Test
	void measurementOfCO2ReportingEmissions_measureTransferredCO2_false_valid() {
		MeasurementOfCO2ReportingEmissions emissions = MeasurementOfCO2ReportingEmissions.builder()
				.type(MonitoringApproachType.MEASUREMENT_CO2)
				.emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.measureTransferredCO2(false)
				.build();
		
		Set<ConstraintViolation<MeasurementOfCO2ReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(0);
	}
	
	@Test
	void measurementOfCO2ReportingEmissions_measureTransferredCO2_true_valid() {
		MeasurementOfCO2ReportingEmissions emissions = MeasurementOfCO2ReportingEmissions.builder()
				.type(MonitoringApproachType.MEASUREMENT_CO2)
				.emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.measureTransferredCO2(true)
				.transferredCO2Emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.build();
		
		Set<ConstraintViolation<MeasurementOfCO2ReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(0);
	}
	
	
	
	@Test
	void measurementOfN2OReportingEmissions_measureTransferredN2O_true_invalid() {
		MeasurementOfN2OReportingEmissions emissions = MeasurementOfN2OReportingEmissions.builder()
				.type(MonitoringApproachType.MEASUREMENT_N2O)
				.emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.measureTransferredN2O(true)
				.build();
		
		Set<ConstraintViolation<MeasurementOfN2OReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{dre.monitoringApproaches.measurementofn2o.transferredn2oemissions}");
	}
	
	@Test
	void measurementOfN2OReportingEmissions_measureTransferredN2O_false_invalid() {
		MeasurementOfN2OReportingEmissions emissions = MeasurementOfN2OReportingEmissions.builder()
				.type(MonitoringApproachType.MEASUREMENT_N2O)
				.emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.measureTransferredN2O(false)
				.transferredN2OEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.build();
		
		Set<ConstraintViolation<MeasurementOfN2OReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{dre.monitoringApproaches.measurementofn2o.transferredn2oemissions}");
	}
	
	@Test
	void measurementOfN2OReportingEmissions_measureTransferredN2O_false_valid() {
		MeasurementOfN2OReportingEmissions emissions = MeasurementOfN2OReportingEmissions.builder()
				.type(MonitoringApproachType.MEASUREMENT_N2O)
				.emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.measureTransferredN2O(false)
				.build();
		
		Set<ConstraintViolation<MeasurementOfN2OReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(0);
	}
	
	@Test
	void measurementOfN2OReportingEmissions_measureTransferredN2O_true_valid() {
		MeasurementOfN2OReportingEmissions emissions = MeasurementOfN2OReportingEmissions.builder()
				.type(MonitoringApproachType.MEASUREMENT_N2O)
				.emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.measureTransferredN2O(true)
				.transferredN2OEmissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
				.build();
		
		Set<ConstraintViolation<MeasurementOfN2OReportingEmissions>> violations = validator.validate(emissions);
		assertThat(violations).hasSize(0);
	}
	
	
	@Test
	void monitoringApproaches_transferredco2_n2o_contains_should_be_invalid() {
		Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.type(DreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
						.build())
				.officialNoticeReason("off_notice")
				.monitoringApproachReportingEmissions(Map.of(MonitoringApproachType.TRANSFERRED_CO2_N2O, FallbackReportingEmissions.builder()
						.emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
						.type(MonitoringApproachType.FALLBACK).build()))
				.informationSources(Set.of("sdfsd"))
				.fee(DreFee.builder().chargeOperator(false).build())
				.build();
		
		Set<ConstraintViolation<Dre>> violations = validator.validate(dre);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("{dre.monitoringApproaches.transferredco2n2o}");
	}
	
	@Test
	void monitoringApproaches_transferredco2_n2o_not_contains_should_be_valid() {
		Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.type(DreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
						.build())
				.officialNoticeReason("off_notice")
				.monitoringApproachReportingEmissions(Map.of(MonitoringApproachType.FALLBACK, FallbackReportingEmissions.builder()
						.emissions(ReportableAndBiomassEmission.builder().reportableEmissions(BigDecimal.ONE).sustainableBiomass(BigDecimal.ONE).build())
						.type(MonitoringApproachType.FALLBACK).build()))
				.informationSources(Set.of("sdfsd"))
				.fee(DreFee.builder().chargeOperator(false).build())
				.build();
		
		Set<ConstraintViolation<Dre>> violations = validator.validate(dre);
		assertThat(violations).hasSize(0);
	}
	
	@Test
	void monitoringApproaches_null_value_should_be_invalid() {
		Map<MonitoringApproachType, DreMonitoringApproachReportingEmissions> approaches = new EnumMap<>(MonitoringApproachType.class);
		approaches.put(MonitoringApproachType.FALLBACK, null);
		Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.type(DreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
						.build())
				.officialNoticeReason("off_notice")
				.monitoringApproachReportingEmissions(approaches)
				.informationSources(Set.of("sdfsd"))
				.fee(DreFee.builder().chargeOperator(false).build())
				.build();
		
		Set<ConstraintViolation<Dre>> violations = validator.validate(dre);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("must not be null");
	}
	
	@Test
	void monitoringApproaches_empty_value_should_be_invalid() {
		Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.type(DreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
						.build())
				.officialNoticeReason("off_notice")
				.monitoringApproachReportingEmissions(Map.of())
				.informationSources(Set.of("sdfsd"))
				.fee(DreFee.builder().chargeOperator(false).build())
				.build();
		
		Set<ConstraintViolation<Dre>> violations = validator.validate(dre);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("must not be empty");
	}
	
	@Test
	void monitoringApproaches_invalid_value_should_be_invalid() {
		Map<MonitoringApproachType, DreMonitoringApproachReportingEmissions> approaches = new EnumMap<>(MonitoringApproachType.class);
		approaches.put(MonitoringApproachType.FALLBACK, FallbackReportingEmissions.builder().build());
		Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.type(DreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
						.build())
				.officialNoticeReason("off_notice")
				.monitoringApproachReportingEmissions(approaches)
				.informationSources(Set.of("sdfsd"))
				.fee(DreFee.builder().chargeOperator(false).build())
				.build();
		
		Set<ConstraintViolation<Dre>> violations = validator.validate(dre);
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("must not be null");
	}
	
}
