package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import net.java.truelicense.core.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.ResolvableType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.EnergyFlows;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.FuelInputDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.MeasurableHeatFlowsDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.WasteGasFlowsDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.FuelInputFlows;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.MeasurableHeatFlows;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.WasteGasFlows;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription.DigitizedMmpInstallationDescription;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription.EntityType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription.FlowDirection;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription.InstallationConnection;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription.InstallationConnectionType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.methodtasks.MethodTask;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.methodtasks.MethodTaskConnection;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.procedures.Procedure;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.procedures.ProcedureType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection46;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection72;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.ImportedMeasurableHeatFlow;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationCategory;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationHierarchicalOrder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.AnnualLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.AnnualLevelType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.*;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels.AnnualProductionLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels.AnnualQuantityDeterminationMethod;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels.QuantityProductDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsFA;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.ImportedExportedAmountsDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelandelectricityexchangeability.FuelAndElectricityExchangeability;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelandelectricityexchangeability.FuelAndElectricityExchangeabilityEnergyFlowDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.*;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeat;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeatEnergyFlowDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeatType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.MeasurableHeat;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported.MeasurableHeatExported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported.MeasurableHeatExportedDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImportedType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.produced.MeasurableHeatProduced;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.AnnexIIPoint1;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.AnnexIIPoint2;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.SpecialProduct;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.SpecialProductType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics.AromaticsQuantitiesOfSupplementalFieldDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics.AromaticsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.dolime.DolimeDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.dolime.DolimeSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols.EthyleneOxideEthyleneGlycolsDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols.EthyleneOxideEthyleneGlycolsDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols.EthyleneOxideEthyleneGlycolsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.lime.LimeDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.lime.LimeSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts.RefineryProductsQuantitiesOfSupplementalFieldDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts.RefineryProductsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.steamcracking.SteamCrackingDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.steamcracking.SteamCrackingSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.vinylchloridemonomer.VinylChlorideMonomerDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.vinylchloridemonomer.VinylChlorideMonomerSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasActivity;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasBalance;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasBalanceEnergyFlowDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasBalanceEnergyFlowDataSourceDetails;
import uk.gov.pmrv.api.permit.validation.DigitizedMmpSectionValidator;
import uk.gov.pmrv.api.permit.validation.datasourceValidation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MonitoringMethodologyPlansValidationTest {

    private static final String DIGITIZED_MMP_FLAG_CONFIG_KEY = "ui.features.digitized-mmp";

    private static Validator validator;

    @InjectMocks
    private DigitizedMmpSectionValidator digitizedMmpSectionValidator;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private DataSourceValidatorFactory dataSourceValidatorFactory;

    @Mock
    private DirectlyAttributableEmissionsPBDataSourceValidator directlyAttributableEmissionsPBDataSourceValidator;

    @Mock
    private FuelInputAndRelevantEmissionFactorPBDatasourceValidator fuelInputAndRelevantEmissionFactorPBDatasourceValidator;

    @Mock
    private FuelInputAndRelevantEmissionFactorFADatasourceValidator fuelInputAndRelevantEmissionFactorFADatasourceValidator;

    @Mock
    private SpecialProductDataSourceValidator specialProductDataSourceValidator;

    @Mock
    private MeasurableHeatImportedDataSourceValidator measurableHeatImportedDataSourceValidator;

    @Mock
    private ImportedExportedMeasurableHeatDataSourceValidator importedExportedMeasurableHeatDataSourceValidator;

    @Mock
    private AnnualActivityLevelDataSourceValidator annualActivityLevelDataSourceValidator;

    @Mock
    private MeasurableHeatExportedDataSourceValidator measurableHeatExportedDataSourceValidator;

    private final Optional<ConfigurationDTO> NON_DIGITIZED_CONFIG_RESULT = Optional.of(new ConfigurationDTO(DIGITIZED_MMP_FLAG_CONFIG_KEY, false));

    private final Optional<ConfigurationDTO> DIGITIZED_CONFIG_RESULT = Optional.of(new ConfigurationDTO(DIGITIZED_MMP_FLAG_CONFIG_KEY, true));

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }


    @Test
    public void test_MonitoringMethodologyPlanVersionValidator_non_digitized() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(NON_DIGITIZED_CONFIG_RESULT);
        PermitContainer permitContainer = PermitContainer.builder().permit(Permit.builder()
            .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                .plans(Set.of(UUID.randomUUID())).build()).build()).build();

        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void testFailure_noPlans_MonitoringMethodologyPlanVersionValidator_non_digitized() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(NON_DIGITIZED_CONFIG_RESULT);
        PermitContainer permitContainer = PermitContainer.builder().permit(Permit.builder()
            .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                .build()).build()).build();

        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void testFailure_emptyPlans_MonitoringMethodologyPlanVersionValidator_non_digitized() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(NON_DIGITIZED_CONFIG_RESULT);
        PermitContainer permitContainer = PermitContainer.builder().permit(Permit.builder()
            .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                .plans(new HashSet<>()).build()).build()).build();

        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_plans_MonitoringMethodologyPlanVersionValidator_digitized() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        PermitContainer permitContainer = PermitContainer.builder()
            .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
            .permit(Permit.builder()
            .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                .plans(Set.of(UUID.randomUUID()))
                .build()).build()).build();

        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void testFailure_noPlans_MonitoringMethodologyPlanVersionValidator_digitized() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        PermitContainer permitContainer = PermitContainer.builder().permit(Permit.builder()
            .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                .build()).build()).build();

        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.NORTHERN_IRELAND);
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorPB.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK).exist(false).build();
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                        .digitizedPlan(DigitizedPlan.builder().subInstallations(
                                List.of(
                                        SubInstallation.builder()
                                                .subInstallationType(SubInstallationType.IRON_CASTING)
                                                .subInstallationNo("not blank value")
                                                .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                        .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                .fuelAndElectricityExchangeability(FuelAndElectricityExchangeability.builder().build())
                                                .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor).build(),
                                        SubInstallation.builder()
                                                .subInstallationType(SubInstallationType.ADIPIC_ACID)
                                                .subInstallationNo("not blank value")
                                                .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                        .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                .wasteGasBalance(WasteGasBalance.builder().wasteGasActivities(Set.of(WasteGasActivity.NO_WASTE_GAS_ACTIVITIES)).build())
                                                .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                .build()
                                )
                        ).methodTask(MethodTask.builder().physicalPartsAndUnitsAnswer(false).build())
                        .build()).build()).build()).build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources(fuelInputAndRelevantEmissionFactor)).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_FA_fuel_no_attribution_invalid() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.ENGLAND);
        DirectlyAttributableEmissionsFA directlyAttributableEmissionsFA = DirectlyAttributableEmissionsFA.builder()
                .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.FALLBACK_APPROACH)
                .build();
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder().subInstallations(
                                                List.of(
                                                        SubInstallation.builder()
                                                                .subInstallationType(SubInstallationType.FUEL_BENCHMARK_CL)
                                                                .subInstallationNo("not blank value")
                                                                .directlyAttributableEmissions(directlyAttributableEmissionsFA)
                                                                .build()

                                                )
                                        ).methodTask(MethodTask.builder().physicalPartsAndUnitsAnswer(false).build())
                                        .build()).build()).build()).build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> digitizedMmpSectionValidator.validate(permitContainer)
        );
        assertTrue(ex.getMessage().contains("Attribution with subInstallationType FUEL_BENCHMARK_CL"));
    }

    @Test
    public void test_DigitizedMmpSectionValidator_FA_no_fuel_no_attribution_valid() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.ENGLAND);

        DirectlyAttributableEmissionsFA directlyAttributableEmissionsFA = DirectlyAttributableEmissionsFA.builder()
                .attribution("")
                .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.FALLBACK_APPROACH)
                .build();
        FuelInputAndRelevantEmissionFactorFA fuelInputAndRelevantEmissionFactorFA= FuelInputAndRelevantEmissionFactorFA.builder().wasteGasesInput(true)
                .fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.FALLBACK_APPROACH).dataSources(any()).build();
        ImportedExportedMeasurableHeat importedExportedMeasurableHeat = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT))
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .build();
        MeasurableHeat measurableHeat = MeasurableHeat.builder()
                .measurableHeatProduced(MeasurableHeatProduced.builder().build())
                .measurableHeatImported(MeasurableHeatImported.builder().measurableHeatImportedActivities(Set.of(MeasurableHeatImportedType.MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED)).build())
                .build();
        AnnualActivityHeatLevel annualActivityHeatLevel = AnnualActivityHeatLevel.builder().annualLevelType(AnnualLevelType.ACTIVITY_HEAT).build();

        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder().subInstallations(
                                                List.of(
                                                        SubInstallation.builder()
                                                                .subInstallationType(SubInstallationType.HEAT_BENCHMARK_CL)
                                                                .subInstallationNo("not blank value")
                                                                .directlyAttributableEmissions(directlyAttributableEmissionsFA)
                                                                .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactorFA)
                                                                .importedExportedMeasurableHeat(importedExportedMeasurableHeat)
                                                                .measurableHeat(measurableHeat)
                                                                .annualLevel(annualActivityHeatLevel)
                                                                .build()

                                                )
                                        ).methodTask(MethodTask.builder().physicalPartsAndUnitsAnswer(false).build())
                                        .build()).build()).build()).build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorFA.class)).thenReturn(fuelInputAndRelevantEmissionFactorFADatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorFADatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorFA) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(MeasurableHeatImported.class)).thenReturn(measurableHeatImportedDataSourceValidator);
        when(measurableHeatImportedDataSourceValidator.validateDataSources(permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getMeasurableHeat().getMeasurableHeatImported())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(AnnualActivityLevel.class)).thenReturn(annualActivityLevelDataSourceValidator);
        when(annualActivityLevelDataSourceValidator.validateDataSources((AnnualActivityHeatLevel) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getAnnualLevel())).thenReturn(true);
        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }



    @Test
    public void testFailure_DigitizedMmpSectionValidator_OpredWithProductBenchmarkSubInstallations() {
        String emitterId = "emitter_id";
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorPB.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK).exist(false).build();
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.OPRED);
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                        .digitizedPlan(DigitizedPlan.builder().subInstallations(
                                List.of(
                                        SubInstallation.builder()
                                                .subInstallationType(SubInstallationType.HOT_METAL)
                                                .subInstallationNo("not blank value")
                                                .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                        .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                .build()
                                )
                        ).methodTask(MethodTask.builder().physicalPartsAndUnitsAnswer(false).build()).build()).build()).build()).build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);

        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void testFailure_DigitizedMmpSectionValidator_WithoutSubInstallations() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.WALES);
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder().subInstallations(new ArrayList<>()).methodTask(MethodTask.builder().build())
                                .build()).build()).build()).build();

        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void testFailure_DigitizedMmpSectionValidator_DuplicateSubInstallationTypes() {
        String emitterId = "emitter_id";
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorPB.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK).exist(false).build();
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.SCOTLAND);
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder().subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.E_PVC)
                                                        .subInstallationNo("not blank value")
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                        .build(),
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.E_PVC)
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .subInstallationNo("not blank value")
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                        .build()
                                        )
                                ).methodTask(MethodTask.builder().build()).build())
                                .build()).build()).build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);

        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void validationSuccessSubInstallationTypeAndSize() {
        List<SubInstallation> subInstallations = new ArrayList<>();
        for (int i=1 ; i<11 ; i++) {
            subInstallations.add(SubInstallation.builder().subInstallationType(SubInstallationType.HYDROGEN).build());
        }
        for (int i=1 ; i<8 ; i++) {
            subInstallations.add(SubInstallation.builder().subInstallationType(SubInstallationType.FUEL_BENCHMARK_CL).build());
        }

        assertTrue(digitizedMmpSectionValidator.validateSubInstallationTypeAndSize(subInstallations));
    }

    @Test
    public void validationFailSubInstallationTypeAndSize() {
        List<SubInstallation> subInstallations = new ArrayList<>();
        for (int i=1 ; i<12 ; i++) {
            subInstallations.add(SubInstallation.builder().subInstallationType(SubInstallationType.HYDROGEN).build());
        }
        for (int i=1 ; i<9 ; i++) {
            subInstallations.add(SubInstallation.builder().subInstallationType(SubInstallationType.FUEL_BENCHMARK_CL).build());
        }

        assertFalse(digitizedMmpSectionValidator.validateSubInstallationTypeAndSize(subInstallations));
    }

    @Test
    public void testFailure_DigitizedMmpSectionValidator_InvalidFuelAndElectricityExchangeabilityInput() {
        String emitterId = "emitter_id";
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorPB.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK).exist(false).build();
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.SCOTLAND);
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder().methodTask(
                                        MethodTask.builder().build()
                                ).subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.FACING_BRICKS)
                                                        .subInstallationNo("not blank value")
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .fuelAndElectricityExchangeability(FuelAndElectricityExchangeability.builder().build())
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                        .build()

                                        )
                                ).build()).build()).build()).build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);

        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void testFailure_DigitizedMmpSectionValidator_InvalidDirectlyAttributableEmissions() {
        String emitterId = "emitter_id";
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorPB.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK).exist(false).build();
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.SCOTLAND);
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder().methodTask(
                                        MethodTask.builder().build()
                                ).subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.HEAT_BENCHMARK_CL)
                                                        .subInstallationNo("not blank value")
                                                        .annualLevel(AnnualActivityHeatLevel.builder().annualLevelType(AnnualLevelType.ACTIVITY_HEAT).build())
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .measurableHeat(MeasurableHeat.builder().measurableHeatProduced(MeasurableHeatProduced.builder().produced(false).build()).build()).build()

                                        )
                                ).build()).build()).build()).build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(AnnualActivityLevel.class)).thenReturn(annualActivityLevelDataSourceValidator);
        when(annualActivityLevelDataSourceValidator.validateDataSources((AnnualActivityHeatLevel) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getAnnualLevel())).thenReturn(true);

        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());

        permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().setDirectlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build());
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }


    @Test
    public void testFailure_DigitizedMmpSectionValidator_InvalidFuelInputDataSourcePB() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.SCOTLAND);

        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactorPB= FuelInputAndRelevantEmissionFactorPB.builder().exist(true).fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK)
                .dataSources(List.of(FuelInputDataSourcePB.builder().weightedEmissionFactor(null).fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION).build(),
                        FuelInputDataSourcePB.builder().weightedEmissionFactor(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN).fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                                .build())).build();
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder().methodTask(
                                        MethodTask.builder().build()
                                ).subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.FACING_BRICKS)
                                                        .subInstallationNo("not blank value")
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactorPB)
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .build())).build()).build()).build()).build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources(fuelInputAndRelevantEmissionFactorPB)).thenReturn(false);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void testFailure_DigitizedMmpSectionValidator_InvalidFuelInputDataSourceFA() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.SCOTLAND);

        FuelInputAndRelevantEmissionFactorFA fuelInputAndRelevantEmissionFactorFA= FuelInputAndRelevantEmissionFactorFA.builder().wasteGasesInput(true)
                .fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.FALLBACK_APPROACH).dataSources(any()).build();
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder().methodTask(
                                        MethodTask.builder().build()
                                ).subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.FACING_BRICKS)
                                                        .subInstallationNo("not blank value")
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactorFA)
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .build())).build()).build()).build()).build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorFA.class)).thenReturn(fuelInputAndRelevantEmissionFactorFADatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorFADatasourceValidator.validateDataSources(fuelInputAndRelevantEmissionFactorFA)).thenReturn(false);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void testFailure_DigitizedMmpSectionValidator_InvalidImportedExportedMeasurableHeatDataSource() {
        String emitterId = "emitter_id";
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactorPB = FuelInputAndRelevantEmissionFactorPB.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK).exist(false).build();
        ImportedExportedMeasurableHeat importedExportedMeasurableHeat =
                ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_FROM_NITRIC_ACID))
                .dataSources(
                        List.of(ImportedExportedMeasurableHeatEnergyFlowDataSource.builder().energyFlowDataSourceNo("no").netMeasurableHeatFlows(AnnexVIISection72.DOCUMENTATION).build(),
                                ImportedExportedMeasurableHeatEnergyFlowDataSource.builder().energyFlowDataSourceNo("no2").build()))
                        .dataSourcesMethodologyAppliedDescription("description")
                        .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                        .methodologyDeterminationDescription("description")
                        .measurableHeatImportedFromPulp(Boolean.FALSE)
                        .pulpMethodologyDeterminationDescription("").build();
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.SCOTLAND);
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder()
                                        .methodTask(MethodTask.builder().build())
                                        .subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.FACING_BRICKS)
                                                        .subInstallationNo("not blank value")
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactorPB)
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .importedExportedMeasurableHeat(importedExportedMeasurableHeat)
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .build()

                                        )
                                ).build()).build()).build()).build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(dataSourceValidatorFactory.getValidator(ImportedExportedMeasurableHeat.class)).thenReturn(importedExportedMeasurableHeatDataSourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources(fuelInputAndRelevantEmissionFactorPB)).thenReturn(true);
        when(importedExportedMeasurableHeatDataSourceValidator.validateDataSources(importedExportedMeasurableHeat)).thenReturn(false);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_InvalidWasteGasBalanceInput() {
        String emitterId = "emitter_id";
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactorPB = FuelInputAndRelevantEmissionFactorPB.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK).exist(false).build();

        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.NORTHERN_IRELAND);
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder()
                                        .methodTask(MethodTask.builder().build())
                                        .subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.ADIPIC_ACID)
                                                        .subInstallationNo("not blank value")
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactorPB)
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .wasteGasBalance(WasteGasBalance.builder()
                                                                .wasteGasActivities(Set.of(WasteGasActivity.WASTE_GAS_FLARED))
                                                                .dataSources(List.of(WasteGasBalanceEnergyFlowDataSource.builder()
                                                                        .wasteGasActivityDetails(Map.of(WasteGasActivity.WASTE_GAS_EXPORTED,WasteGasBalanceEnergyFlowDataSourceDetails.builder().build()))
                                                                .build())).build())
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .build()
                                        )
                                ).build()).build()).build()).build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources(fuelInputAndRelevantEmissionFactorPB)).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);

        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());

        WasteGasBalance wasteGasBalance = WasteGasBalance.builder()
                .wasteGasActivities(Set.of(WasteGasActivity.WASTE_GAS_FLARED, WasteGasActivity.WASTE_GAS_CONSUMED))
                .dataSources(List.of(WasteGasBalanceEnergyFlowDataSource.builder()
                        .wasteGasActivityDetails(Map.of(WasteGasActivity.WASTE_GAS_FLARED,WasteGasBalanceEnergyFlowDataSourceDetails.builder().build(),
                                WasteGasActivity.WASTE_GAS_CONSUMED,WasteGasBalanceEnergyFlowDataSourceDetails.builder().build()))
                        .build())).build();

        permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().setWasteGasBalance(wasteGasBalance);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }


    @Test
    public void test_DigitizedMmpSectionValidator_validAnnualProductionLevel() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        PermitContainer permitContainer = getAnnualLevelPermitContainer(SubInstallationType.HOT_METAL,AnnualLevelType.PRODUCTION);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_invalidAnnualProductionLevel() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorFA.class)).thenReturn(fuelInputAndRelevantEmissionFactorFADatasourceValidator);
        PermitContainer permitContainer = getAnnualLevelPermitContainer(SubInstallationType.HEAT_BENCHMARK_CL,AnnualLevelType.PRODUCTION);
        when(dataSourceValidatorFactory.getValidator(MeasurableHeatImported.class)).thenReturn(measurableHeatImportedDataSourceValidator);
        when(measurableHeatImportedDataSourceValidator.validateDataSources(permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getMeasurableHeat().getMeasurableHeatImported())).thenReturn(true);
        when(fuelInputAndRelevantEmissionFactorFADatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorFA) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_validAnnualActivityHeatLevel() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorFA.class)).thenReturn(fuelInputAndRelevantEmissionFactorFADatasourceValidator);
        PermitContainer permitContainer = getAnnualLevelPermitContainer(SubInstallationType.HEAT_BENCHMARK_CL,AnnualLevelType.ACTIVITY_HEAT);
        when(dataSourceValidatorFactory.getValidator(MeasurableHeatImported.class)).thenReturn(measurableHeatImportedDataSourceValidator);
        when(measurableHeatImportedDataSourceValidator.validateDataSources(permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getMeasurableHeat().getMeasurableHeatImported())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(AnnualActivityLevel.class)).thenReturn(annualActivityLevelDataSourceValidator);
        when(annualActivityLevelDataSourceValidator.validateDataSources((AnnualActivityHeatLevel) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getAnnualLevel())).thenReturn(true);
        when(fuelInputAndRelevantEmissionFactorFADatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorFA) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_invalidAnnualActivityHeatLevel() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        PermitContainer permitContainer = getAnnualLevelPermitContainer(SubInstallationType.HOT_METAL,AnnualLevelType.ACTIVITY_HEAT);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }


    @Test
    public void test_DigitizedMmpSectionValidator_validAnnualActivityFuelLevel() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorFA.class)).thenReturn(fuelInputAndRelevantEmissionFactorFADatasourceValidator);
        PermitContainer permitContainer = getAnnualLevelPermitContainer(SubInstallationType.FUEL_BENCHMARK_CL,AnnualLevelType.ACTIVITY_FUEL);
        when(dataSourceValidatorFactory.getValidator(AnnualActivityLevel.class)).thenReturn(annualActivityLevelDataSourceValidator);
        when(annualActivityLevelDataSourceValidator.validateDataSources((AnnualActivityFuelLevel) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getAnnualLevel())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(MeasurableHeatExported.class)).thenReturn(measurableHeatExportedDataSourceValidator);
        when(measurableHeatExportedDataSourceValidator.validateDataSources(permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getMeasurableHeat().getMeasurableHeatExported())).thenReturn(true);
        when(fuelInputAndRelevantEmissionFactorFADatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorFA) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_invalidAnnualActivityFuelLevel() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        PermitContainer permitContainer = getAnnualLevelPermitContainer(SubInstallationType.ADIPIC_ACID,AnnualLevelType.ACTIVITY_FUEL);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_validAnnualActivityProcessLevel() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        PermitContainer permitContainer = getAnnualLevelPermitContainer(SubInstallationType.PROCESS_EMISSIONS_CL,AnnualLevelType.ACTIVITY_PROCESS);
        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_invalidAnnualActivityProcessLevel() {
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        PermitContainer permitContainer = getAnnualLevelPermitContainer(SubInstallationType.ADIPIC_ACID,AnnualLevelType.ACTIVITY_PROCESS);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }


    @Test
    public void test_DigitizedMmpSectionValidator_InvalidSpecialProductInput_RefineryProducts() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.NORTHERN_IRELAND);
        when(dataSourceValidatorFactory.getValidator(SpecialProduct.class)).thenReturn(specialProductDataSourceValidator);

        RefineryProductsSP refineryProductsSP = RefineryProductsSP.builder()
                .specialProductType(SpecialProductType.REFINERY_PRODUCTS)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .refineryProductsRelevantCWTFunctions(Set.of(AnnexIIPoint1.ASPHALT_MANUFACTURE, AnnexIIPoint1.AIR_SEPARATION))
                .refineryProductsDataSources(List.of(RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder().
                        dataSourceNo("1")
                        .details(Map.of(AnnexIIPoint1.ASPHALT_MANUFACTURE, AnnexVIISection44.INDIRECT_DETERMINATION,AnnexIIPoint1.C4_ISOMERISATION, AnnexVIISection44.LEGAL_METROLOGICAL_CONTROL)).build()))
                .build();

        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder().methodTask(MethodTask.builder().build()).subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.REFINERY_PRODUCTS)
                                                        .subInstallationNo("not blank value")
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .fuelAndElectricityExchangeability(FuelAndElectricityExchangeability.builder().build())
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .specialProduct(refineryProductsSP).build()
                                        )
                                ).build()).build()).build()).build();
        when(specialProductDataSourceValidator.validateDataSources(refineryProductsSP)).thenReturn(false);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }


    @Test
    public void test_DigitizedMmpSectionValidator_InvalidSpecialProductInput_Aromatics() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.NORTHERN_IRELAND);
        when(dataSourceValidatorFactory.getValidator(SpecialProduct.class)).thenReturn(specialProductDataSourceValidator);

        AromaticsSP aromaticsSP = AromaticsSP.builder()
                .specialProductType(SpecialProductType.AROMATICS)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .relevantCWTFunctions(Set.of(AnnexIIPoint2.HYDRODEALKYLATION, AnnexIIPoint2.PARAXYLENE_PRODUCTION))
                .dataSources(List.of(AromaticsQuantitiesOfSupplementalFieldDataSource.builder().
                        dataSourceNo("1")
                        .details(Map.of(AnnexIIPoint2.HYDRODEALKYLATION, AnnexVIISection44.INDIRECT_DETERMINATION,AnnexIIPoint2.TDP_TDA, AnnexVIISection44.LEGAL_METROLOGICAL_CONTROL)).build()))
                .build();
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder()
                                        .methodTask(MethodTask.builder().build())
                                        .subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.AROMATICS)
                                                        .subInstallationNo("not blank value")
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .fuelAndElectricityExchangeability(FuelAndElectricityExchangeability.builder().build())
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .specialProduct(aromaticsSP).build()
                                        )
                                ).build()).build()).build()).build();
        when(specialProductDataSourceValidator.validateDataSources(aromaticsSP)).thenReturn(false);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_InvalidSpecialProductInput_EthyleneOxideEthyleneGlycols() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.NORTHERN_IRELAND);
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder()
                                        .methodTask(MethodTask.builder().build())
                                        .subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.ETHYLENE_OXIDE_ETHYLENE_GLYCOLS)
                                                        .subInstallationNo("not blank value")
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .fuelAndElectricityExchangeability(FuelAndElectricityExchangeability.builder().build())
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .specialProduct(EthyleneOxideEthyleneGlycolsSP.builder()
                                                                .specialProductType(SpecialProductType.ETHYLENE_OXIDE_ETHYLENE_GLYCOLS)
                                                                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                                                                .methodologyAppliedDescription("description")
                                                                .dataSources(List.of(EthyleneOxideEthyleneGlycolsDataSource.builder().
                                                                        dataSourceNo("1")
                                                                        .details(EthyleneOxideEthyleneGlycolsDetails.builder().build()).build()))
                                                                .build()).build()
                                        )
                                ).build()).build()).build()).build();


        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_InvalidEnergyFlowsDatasources() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.NORTHERN_IRELAND);

        FuelInputDataSource fuelInputDataSource1 = new FuelInputDataSource();
        fuelInputDataSource1.setDataSourceNumber("1");
        fuelInputDataSource1.setFuelInput(AnnexVIISection44.LEGAL_METROLOGICAL_CONTROL);
        FuelInputDataSource fuelInputDataSource2 = new FuelInputDataSource();
        fuelInputDataSource2.setDataSourceNumber("2");
        List<FuelInputDataSource> fuelInputDataSources = List.of(fuelInputDataSource1, fuelInputDataSource2);

        MeasurableHeatFlowsDataSource measurableHeatFlowsDataSource1 = new MeasurableHeatFlowsDataSource();
        measurableHeatFlowsDataSource1.setDataSourceNumber("1");
        measurableHeatFlowsDataSource1.setQuantification(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A);
        MeasurableHeatFlowsDataSource measurableHeatFlowsDataSource2 = new MeasurableHeatFlowsDataSource();
        measurableHeatFlowsDataSource2.setDataSourceNumber("2");
        List<MeasurableHeatFlowsDataSource> measurableHeatFlowsDataSources = List.of(measurableHeatFlowsDataSource1, measurableHeatFlowsDataSource2);

        WasteGasFlowsDataSource wasteGasFlowsDataSource1 = new WasteGasFlowsDataSource();
        wasteGasFlowsDataSource1.setDataSourceNumber("1");
        wasteGasFlowsDataSource1.setEnergyContent(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61);
        WasteGasFlowsDataSource wasteGasFlowsDataSource2 = new WasteGasFlowsDataSource();
        wasteGasFlowsDataSource2.setDataSourceNumber("2");
        List<WasteGasFlowsDataSource> wasteGasFlowsDataSources = List.of(wasteGasFlowsDataSource1, wasteGasFlowsDataSource2);

        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder()
                                        .methodTask(MethodTask.builder().build())
                                        .energyFlows(EnergyFlows.builder()
                                                .fuelInputFlows(FuelInputFlows.builder()
                                                        .fuelInputDataSources(fuelInputDataSources)
                                                        .methodologyAppliedDescription("methodologyAppliedDescription")
                                                        .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                                                        .build())
                                                .measurableHeatFlows(MeasurableHeatFlows.builder()
                                                        .measurableHeatFlowsRelevant(true)
                                                        .measurableHeatFlowsDataSources(measurableHeatFlowsDataSources)
                                                        .build())
                                                .wasteGasFlows(WasteGasFlows.builder()
                                                        .wasteGasFlowsRelevant(true)
                                                        .wasteGasFlowsDataSources(wasteGasFlowsDataSources)
                                                        .build())
                                                .build()).build()
                                ).build()
                        ).build())
                .build();

        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_validEnergyFlowsDatasources() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.NORTHERN_IRELAND);

        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorPB.builder()
                .fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK)
                .exist(false).build();

        FuelInputDataSource fuelInputDataSource1 = new FuelInputDataSource();
        fuelInputDataSource1.setDataSourceNumber("1");
        fuelInputDataSource1.setFuelInput(AnnexVIISection44.LEGAL_METROLOGICAL_CONTROL);
        fuelInputDataSource1.setEnergyContent(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61);
        FuelInputDataSource fuelInputDataSource2 = new FuelInputDataSource();
        fuelInputDataSource2.setDataSourceNumber("2");
        fuelInputDataSource2.setEnergyContent(AnnexVIISection46.CONSTANT_VALUES_STANDARD_SUPPLIER);
        List<FuelInputDataSource> fuelInputDataSources = List.of(fuelInputDataSource1, fuelInputDataSource2);

        MeasurableHeatFlowsDataSource measurableHeatFlowsDataSource1 = new MeasurableHeatFlowsDataSource();
        measurableHeatFlowsDataSource1.setDataSourceNumber("1");
        measurableHeatFlowsDataSource1.setQuantification(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A);
        measurableHeatFlowsDataSource1.setNet(AnnexVIISection72.PROXY_MEASURED_EFFICIENCY);
        List<MeasurableHeatFlowsDataSource> measurableHeatFlowsDataSources = List.of(measurableHeatFlowsDataSource1);

        WasteGasFlowsDataSource wasteGasFlowsDataSource1 = new WasteGasFlowsDataSource();
        wasteGasFlowsDataSource1.setDataSourceNumber("1");
        wasteGasFlowsDataSource1.setQuantification(AnnexVIISection44.METHOD_MONITORING_PLAN);
        wasteGasFlowsDataSource1.setEnergyContent(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61);
        WasteGasFlowsDataSource wasteGasFlowsDataSource2 = new WasteGasFlowsDataSource();
        wasteGasFlowsDataSource2.setDataSourceNumber("2");
        wasteGasFlowsDataSource2.setQuantification(AnnexVIISection44.INDIRECT_DETERMINATION);
        List<WasteGasFlowsDataSource> wasteGasFlowsDataSources = List.of(wasteGasFlowsDataSource1, wasteGasFlowsDataSource2);

        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder()
                                        .methodTask(MethodTask.builder().build())
                                        .energyFlows(EnergyFlows.builder()
                                                .fuelInputFlows(FuelInputFlows.builder()
                                                        .fuelInputDataSources(fuelInputDataSources)
                                                        .methodologyAppliedDescription("methodologyAppliedDescription")
                                                        .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                                                        .build())
                                                .measurableHeatFlows(MeasurableHeatFlows.builder()
                                                        .measurableHeatFlowsRelevant(true)
                                                        .measurableHeatFlowsDataSources(measurableHeatFlowsDataSources)
                                                        .build())
                                                .wasteGasFlows(WasteGasFlows.builder()
                                                        .wasteGasFlowsRelevant(true)
                                                        .wasteGasFlowsDataSources(wasteGasFlowsDataSources)
                                                        .build())
                                                .build())
                                        .subInstallations(
                                                List.of(
                                                        SubInstallation.builder()
                                                                .subInstallationType(SubInstallationType.IRON_CASTING)
                                                                .subInstallationNo("not blank value")
                                                                .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                                .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                        .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                                .fuelAndElectricityExchangeability(FuelAndElectricityExchangeability.builder().build())
                                                                .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                                .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor).build(),
                                                        SubInstallation.builder()
                                                                .subInstallationType(SubInstallationType.ADIPIC_ACID)
                                                                .subInstallationNo("not blank value")
                                                                .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder()
                                                                        .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK)
                                                                        .build())
                                                                .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                        .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                                .wasteGasBalance(WasteGasBalance.builder().wasteGasActivities(Set.of(WasteGasActivity.NO_WASTE_GAS_ACTIVITIES)).build())
                                                                .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                                .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                                .build()
                                                )
                                        )
                                        .methodTask(MethodTask.builder().physicalPartsAndUnitsAnswer(false).build())
                                        .build()
                                ).build()
                        ).build())
                .build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources(fuelInputAndRelevantEmissionFactor)).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_validEnergyFlowsDatasources_without_measurableHeatFlowsDatasources() {
        String emitterId = "emitter_id";
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.NORTHERN_IRELAND);

        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorPB.builder()
                .fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK)
                .exist(false).build();

        FuelInputDataSource fuelInputDataSource1 = new FuelInputDataSource();
        fuelInputDataSource1.setDataSourceNumber("1");
        fuelInputDataSource1.setFuelInput(AnnexVIISection44.LEGAL_METROLOGICAL_CONTROL);
        fuelInputDataSource1.setEnergyContent(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61);
        FuelInputDataSource fuelInputDataSource2 = new FuelInputDataSource();
        fuelInputDataSource2.setDataSourceNumber("2");
        fuelInputDataSource2.setEnergyContent(AnnexVIISection46.CONSTANT_VALUES_STANDARD_SUPPLIER);
        List<FuelInputDataSource> fuelInputDataSources = List.of(fuelInputDataSource1, fuelInputDataSource2);

        WasteGasFlowsDataSource wasteGasFlowsDataSource1 = new WasteGasFlowsDataSource();
        wasteGasFlowsDataSource1.setDataSourceNumber("1");
        wasteGasFlowsDataSource1.setQuantification(AnnexVIISection44.METHOD_MONITORING_PLAN);
        wasteGasFlowsDataSource1.setEnergyContent(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61);
        WasteGasFlowsDataSource wasteGasFlowsDataSource2 = new WasteGasFlowsDataSource();
        wasteGasFlowsDataSource2.setDataSourceNumber("2");
        wasteGasFlowsDataSource2.setQuantification(AnnexVIISection44.INDIRECT_DETERMINATION);
        List<WasteGasFlowsDataSource> wasteGasFlowsDataSources = List.of(wasteGasFlowsDataSource1, wasteGasFlowsDataSource2);

        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder()
                                        .methodTask(MethodTask.builder().build())
                                        .energyFlows(EnergyFlows.builder()
                                                .fuelInputFlows(FuelInputFlows.builder()
                                                        .fuelInputDataSources(fuelInputDataSources)
                                                        .methodologyAppliedDescription("methodologyAppliedDescription")
                                                        .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                                                        .build())
                                                .measurableHeatFlows(MeasurableHeatFlows.builder()
                                                        .measurableHeatFlowsRelevant(false)
                                                        .build())
                                                .wasteGasFlows(WasteGasFlows.builder()
                                                        .wasteGasFlowsRelevant(true)
                                                        .wasteGasFlowsDataSources(wasteGasFlowsDataSources)
                                                        .build())
                                                .build())
                                        .subInstallations(
                                                List.of(
                                                        SubInstallation.builder()
                                                                .subInstallationType(SubInstallationType.IRON_CASTING)
                                                                .subInstallationNo("not blank value")
                                                                .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                                .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                        .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                                .fuelAndElectricityExchangeability(FuelAndElectricityExchangeability.builder().build())
                                                                .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                                .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor).build(),
                                                        SubInstallation.builder()
                                                                .subInstallationType(SubInstallationType.ADIPIC_ACID)
                                                                .subInstallationNo("not blank value")
                                                                .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder()
                                                                        .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK)
                                                                        .build())
                                                                .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                        .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                                .wasteGasBalance(WasteGasBalance.builder().wasteGasActivities(Set.of(WasteGasActivity.NO_WASTE_GAS_ACTIVITIES)).build())
                                                                .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                                .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                                .build()
                                                )
                                        )
                                        .methodTask(MethodTask.builder().physicalPartsAndUnitsAnswer(false).build())
                                        .build()
                                ).build()
                        ).build())
                .build();

        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources(fuelInputAndRelevantEmissionFactor)).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }


    @Test
    public void validationReturnsErrorMessageIfANestedModelHasAnInvalidValue() throws IOException {
        final DigitizedPlan digitizedPlan = DigitizedPlan.builder().installationDescription(DigitizedMmpInstallationDescription.builder().description("").
                connections(List.of(InstallationConnection.builder()
                        .connectionType(InstallationConnectionType.INTERMEDIATE_PRODUCTS_COVERED_BY_PRODUCT_BENCHMARKS)
                        .entityType(EntityType.ETS_INSTALLATION)
                        .flowDirection(null).build())).build()).procedures(getProceduresFromFile("digitizedplan_procedures")).build();

        Set<String> messages = messages(validator.validate(digitizedPlan));

        assertThat(messages.size()).isEqualTo(3);
        assertThat(messages).contains("{permit.monitoringmethodologyplans.digitized.description.connection.invalid_installationId}");
        assertThat(messages).contains("must not be blank");
        assertThat(messages).contains("must not be null");
    }

    @Test
    public void validationReturnsNoErrorMessagesIfANestedModelIsValid() throws IOException {
        final DigitizedPlan digitizedPlan = DigitizedPlan.builder()
                .methodTask(MethodTask.builder().physicalPartsAndUnitsAnswer(false).avoidDoubleCountToggle(false).build())
                .installationDescription(DigitizedMmpInstallationDescription.builder().description("not blank value")
                .connections(List.of(InstallationConnection.builder()
                                .connectionNo("not blank value")
                                .installationOrEntityName("not blank value")
                                .entityType(EntityType.NITRIC_ACID_PRODUCTION)
                                .installationId("not blank value")
                                .connectionType(InstallationConnectionType.MEASURABLE_HEAT)
                                .flowDirection(FlowDirection.EXPORT)
                                .contactPersonName("not blank value")
                                .emailAddress("test@test.com")
                                .phoneNumber("not blank value")
                        .build())).build()).procedures(getProceduresFromFile("digitizedplan_procedures")).build();

        Set<String> messages = messages(validator.validate(digitizedPlan));

        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_AnnualProductionLevel() {
        final AnnualProductionLevel annualProductionLevel = AnnualProductionLevel.builder()
                .annualLevelType(AnnualLevelType.PRODUCTION)
                .quantityProductDataSources(List.of(QuantityProductDataSource.builder().quantityProductDataSourceNo("1").quantityProduct(AnnexVIISection44.INDIRECT_DETERMINATION).build()))
                .annualQuantityDeterminationMethod(AnnualQuantityDeterminationMethod.AGGREGATION_METERING_QUANTITIES)
                .methodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .trackingMethodologyDescription("description").build();

        Set<String> messages = messages(validator.validate(annualProductionLevel));

        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_AnnualProductionLevel() {
        final AnnualProductionLevel annualProductionLevel = AnnualProductionLevel.builder()
                .annualLevelType(null)
                .quantityProductDataSources(new ArrayList<>())
                .annualQuantityDeterminationMethod(null)
                .methodologyAppliedDescription("")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .trackingMethodologyDescription("description").build();

        Set<String> messages = messages(validator.validate(annualProductionLevel));

        assertThat(messages.size()).isEqualTo(4);
        assertThat(messages).contains("size must be between 1 and 6");
        assertThat(messages).contains("must not be blank");
        assertThat(messages).contains("must not be null");
        assertThat(messages).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.annuallevel.invalid_input_hierarchical_order}");
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_MeasurableHeatExported() {
        final MeasurableHeatExported measurableHeatExported = MeasurableHeatExported.builder()
                .measurableHeatExported(true)
                .dataSources(List.of(MeasurableHeatExportedDataSource.builder().dataSourceNo("1")
                        .netMeasurableHeatFlows(AnnexVIISection72.MEASUREMENTS).build()))
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .methodologyDeterminationEmissionDescription("description")
                .build();

        Set<String> messages = messages(validator.validate(measurableHeatExported));
        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_MeasurableHeatExported() {
        final MeasurableHeatExported measurableHeatExported = MeasurableHeatExported.builder()
                .measurableHeatExported(true)
                .dataSources(List.of(MeasurableHeatExportedDataSource.builder().dataSourceNo("1")
                        .netMeasurableHeatFlows(AnnexVIISection72.MEASUREMENTS).build()))
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .methodologyDeterminationEmissionDescription("")
                .build();

        Set<String> messages = messages(validator.validate(measurableHeatExported));
        assertThat(messages.size()).isEqualTo(1);
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_FuelAndElectricityExchangeability() {
        final FuelAndElectricityExchangeability fuelAndElectricityExchangeability = FuelAndElectricityExchangeability.builder()
                .fuelAndElectricityExchangeabilityEnergyFlowDataSources(List.of(FuelAndElectricityExchangeabilityEnergyFlowDataSource.builder().energyFlowDataSourceNo("1").relevantElectricityConsumption(AnnexVIISection45.LEGAL_METROLOGICAL_CONTROL_READING).build()))
                .methodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build()).build();


        Set<String> messages = messages(validator.validate(fuelAndElectricityExchangeability));

        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_FuelAndElectricityExchangeability() {
        final FuelAndElectricityExchangeability fuelAndElectricityExchangeability = FuelAndElectricityExchangeability.builder()
                .fuelAndElectricityExchangeabilityEnergyFlowDataSources(new ArrayList<>())
                .methodologyAppliedDescription("")
                .hierarchicalOrder(null).build();


        Set<String> messages = messages(validator.validate(fuelAndElectricityExchangeability));

        assertThat(messages.size()).isEqualTo(3);
        assertThat(messages).contains("size must be between 1 and 6");
        assertThat(messages).contains("must not be blank");
        assertThat(messages).contains("must not be null");
    }



    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_ImportedMeasurableHeatFlow() {
        final ImportedMeasurableHeatFlow importedMeasurableHeatFlow1 = ImportedMeasurableHeatFlow.builder()
                .exist(false)
                .methodologyAppliedDescription(null)
                .supportingFiles(Collections.emptySet()).build();

        final ImportedMeasurableHeatFlow importedMeasurableHeatFlow2 = ImportedMeasurableHeatFlow.builder()
                .exist(true)
                .methodologyAppliedDescription("description")
                .supportingFiles(Collections.emptySet()).build();


        Set<String> messages = messages(validator.validate(importedMeasurableHeatFlow1));
        messages.addAll(messages(validator.validate(importedMeasurableHeatFlow2)));

        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_ImportedMeasurableHeatFlow() {
        final ImportedMeasurableHeatFlow importedMeasurableHeatFlow1 = ImportedMeasurableHeatFlow.builder()
                .exist(false)
                .methodologyAppliedDescription("errory_description").build();

        final ImportedMeasurableHeatFlow importedMeasurableHeatFlow2 = ImportedMeasurableHeatFlow.builder()
                .exist(true)
                .methodologyAppliedDescription("")
                .supportingFiles(Collections.emptySet()).build();


        Set<String> messages1 = messages(validator.validate(importedMeasurableHeatFlow1));
        Set<String> messages2 = messages(validator.validate(importedMeasurableHeatFlow2));

        assertThat(messages1.size()).isEqualTo(1);
        assertThat(messages1).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.importedMeasurableHeatFlow.invalid_input}");

        assertThat(messages2.size()).isEqualTo(1);
        assertThat(messages2).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.importedMeasurableHeatFlow.invalid_input}");
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_DirectlyAttributableEmissionsPB() {
        final DirectlyAttributableEmissionsPB directlyAttributableEmissionsPB1 = DirectlyAttributableEmissionsPB.builder()
                .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK)
                .attribution("attribution")
                .furtherInternalSourceStreamsRelevant(false)
                .transferredCO2ImportedOrExportedRelevant(false)
                .build();

        final DirectlyAttributableEmissionsPB directlyAttributableEmissionsPB2 = DirectlyAttributableEmissionsPB.builder()
                .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK)
                .attribution("attribution")
                .furtherInternalSourceStreamsRelevant(true)
                .dataSources(List.of(ImportedExportedAmountsDataSource.builder().importedExportedAmountsDataSourceNo("no").amounts(AnnexVIISection44.INDIRECT_DETERMINATION).build()))
                .methodologyAppliedDescription("description")
                .transferredCO2ImportedOrExportedRelevant(true)
                .amountsMonitoringDescription("description")
                .build();


        Set<String> messages = messages(validator.validate(directlyAttributableEmissionsPB1));
        messages.addAll(messages(validator.validate(directlyAttributableEmissionsPB2)));

        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_DirectlyAttributableEmissionsPB() {
        final DirectlyAttributableEmissionsPB directlyAttributableEmissionsPB1 = DirectlyAttributableEmissionsPB.builder()
                .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK)
                .attribution("")
                .furtherInternalSourceStreamsRelevant(true)
                .dataSources(new ArrayList<>())
                .methodologyAppliedDescription("")
                .transferredCO2ImportedOrExportedRelevant(false)
                .build();

        final DirectlyAttributableEmissionsPB directlyAttributableEmissionsPB2 = DirectlyAttributableEmissionsPB.builder()
                .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK)
                .attribution("")
                .furtherInternalSourceStreamsRelevant(true)
                .dataSources(List.of(ImportedExportedAmountsDataSource.builder().importedExportedAmountsDataSourceNo("no").amounts(AnnexVIISection44.INDIRECT_DETERMINATION).build()))
                .methodologyAppliedDescription("description")
                .transferredCO2ImportedOrExportedRelevant(true)
                .amountsMonitoringDescription("")
                .build();

        Set<String> messages1 = messages(validator.validate(directlyAttributableEmissionsPB1));
        Set<String> messages2 = messages(validator.validate(directlyAttributableEmissionsPB2));

        assertThat(messages1.size()).isEqualTo(1);
        assertThat(messages1).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.direcltyAttributableEmmisions.invalid_input_1}");

        assertThat(messages2.size()).isEqualTo(1);
        assertThat(messages2).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.direcltyAttributableEmmisions.invalid_input_2}");
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_DirectlyAttributableEmissionsFA() {
        final DirectlyAttributableEmissionsFA directlyAttributableEmissionsFA = DirectlyAttributableEmissionsFA.builder()
                .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.FALLBACK_APPROACH)
                .attribution("")
                .build();

        Set<String> messages = messages(validator.validate(directlyAttributableEmissionsFA));

        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_DirectlyAttributableEmissionsFA() {
        final DirectlyAttributableEmissionsFA directlyAttributableEmissionsFA = DirectlyAttributableEmissionsFA.builder()
                .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.FALLBACK_APPROACH)
                .attribution("attribution")
                .build();

        Set<String> messages = messages(validator.validate(directlyAttributableEmissionsFA));

        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_FuelInputAndRelevantEmissionFactorPB() {
        final FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor1 = FuelInputAndRelevantEmissionFactorPB.builder()
                .exist(false).fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK).build();

        final FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor2 = FuelInputAndRelevantEmissionFactorPB.builder()
                .exist(true)
                .fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK)
                .dataSources(List.of(FuelInputDataSourcePB.builder().fuelInputDataSourceNo("no").fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION).weightedEmissionFactor(AnnexVIISection46.CONSTANT_VALUES_SCIENTIFIC_EVIDENCE).build()))
                .methodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build()).build();


        Set<String> messages = messages(validator.validate(fuelInputAndRelevantEmissionFactor1));
        messages.addAll(messages(validator.validate(fuelInputAndRelevantEmissionFactor2)));

        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_FuelInputAndRelevantEmissionFactorPB() {
        final FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor1 = FuelInputAndRelevantEmissionFactorPB.builder()
                .exist(false)
                .fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK)
                .methodologyAppliedDescription("not blank value").build();

        final FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor2 = FuelInputAndRelevantEmissionFactorPB.builder()
                .exist(true)
                .dataSources(new ArrayList<>())
                .methodologyAppliedDescription("")
                .fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build()).build();

        Set<String> messages1 = messages(validator.validate(fuelInputAndRelevantEmissionFactor1));
        Set<String> messages2 = messages(validator.validate(fuelInputAndRelevantEmissionFactor2));

        assertThat(messages1.size()).isEqualTo(1);
        assertThat(messages1).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.importedExportedAmountsDataSource.invalid_input}");

        assertThat(messages2.size()).isEqualTo(1);
        assertThat(messages2).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.importedExportedAmountsDataSource.invalid_input}");
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_ImportedExportedMeasurableHeat() {
        final ImportedExportedMeasurableHeat importedExportedMeasurableHeat1 = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build();

        final ImportedExportedMeasurableHeat importedExportedMeasurableHeat2 = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_FROM_NITRIC_ACID))
                .dataSources(List.of(ImportedExportedMeasurableHeatEnergyFlowDataSource.builder().energyFlowDataSourceNo("no").netMeasurableHeatFlows(AnnexVIISection72.DOCUMENTATION).build()))
                .dataSourcesMethodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyDeterminationDescription("description")
                .measurableHeatImportedFromPulp(Boolean.FALSE)
                .pulpMethodologyDeterminationDescription("").build();

        final ImportedExportedMeasurableHeat importedExportedMeasurableHeat3 = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_FROM_NITRIC_ACID))
                .dataSources(List.of(ImportedExportedMeasurableHeatEnergyFlowDataSource.builder().energyFlowDataSourceNo("no").netMeasurableHeatFlows(AnnexVIISection72.DOCUMENTATION).build()))
                .dataSourcesMethodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyDeterminationDescription("description")
                .measurableHeatImportedFromPulp(Boolean.TRUE)
                .pulpMethodologyDeterminationDescription("description").build();


        Set<String> messages = messages(validator.validate(importedExportedMeasurableHeat1));
        messages.addAll(messages(validator.validate(importedExportedMeasurableHeat2)));
        messages.addAll(messages(validator.validate(importedExportedMeasurableHeat3)));

        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_ImportedExportedMeasurableHeat() {
        final ImportedExportedMeasurableHeat importedExportedMeasurableHeat1 = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Collections.emptySet()).build();

        final ImportedExportedMeasurableHeat importedExportedMeasurableHeat2 = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT))
                .dataSources(List.of(ImportedExportedMeasurableHeatEnergyFlowDataSource.builder().energyFlowDataSourceNo("no").netMeasurableHeatFlows(AnnexVIISection72.DOCUMENTATION).build()))
                .dataSourcesMethodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyDeterminationDescription("description")
                .measurableHeatImportedFromPulp(Boolean.FALSE)
                .pulpMethodologyDeterminationDescription("").build();

        final ImportedExportedMeasurableHeat importedExportedMeasurableHeat3 = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT, ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_FROM_PULP))
                .dataSources(List.of(ImportedExportedMeasurableHeatEnergyFlowDataSource.builder().energyFlowDataSourceNo("no").netMeasurableHeatFlows(AnnexVIISection72.DOCUMENTATION).build()))
                .dataSourcesMethodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyDeterminationDescription("description")
                .measurableHeatImportedFromPulp(Boolean.FALSE)
                .pulpMethodologyDeterminationDescription("").build();

        final ImportedExportedMeasurableHeat importedExportedMeasurableHeat4 = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_FROM_NITRIC_ACID))
                .dataSources(List.of(ImportedExportedMeasurableHeatEnergyFlowDataSource.builder().energyFlowDataSourceNo("no").netMeasurableHeatFlows(AnnexVIISection72.DOCUMENTATION).build()))
                .dataSourcesMethodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyDeterminationDescription("description")
                .measurableHeatImportedFromPulp(Boolean.TRUE)
                .pulpMethodologyDeterminationDescription("").build();


        Set<String> messages1 = messages(validator.validate(importedExportedMeasurableHeat1));
        Set<String> messages2 = messages(validator.validate(importedExportedMeasurableHeat2));
        Set<String> messages3 = messages(validator.validate(importedExportedMeasurableHeat3));
        Set<String> messages4 = messages(validator.validate(importedExportedMeasurableHeat4));

        assertThat(messages1.size()).isEqualTo(3); //@SpELExpression 1 and 2 should contain empty check in order to work, even if @NotEmpty already exists
        assertThat(messages1).contains("must not be empty");

        assertThat(messages2.size()).isEqualTo(1);
        assertThat(messages2).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.importedExportedMeasurableeat.invalid_input_1}");

        assertThat(messages3.size()).isEqualTo(2);
        assertThat(messages3).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.importedExportedMeasurableeat.invalid_input_1}");
        assertThat(messages3).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.importedExportedMeasurableeat.invalid_input_2}");

        assertThat(messages4.size()).isEqualTo(1);
        assertThat(messages4).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.importedExportedMeasurableeat.invalid_input_3}");
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_WasteGasBalance() {
        final WasteGasBalance wasteGasBalance1 = WasteGasBalance.builder()
                .wasteGasActivities(Set.of(WasteGasActivity.NO_WASTE_GAS_ACTIVITIES)).build();

        final WasteGasBalance wasteGasBalance2 = WasteGasBalance.builder()
                .wasteGasActivities(Set.of(WasteGasActivity.WASTE_GAS_PRODUCED))
                .dataSources(List.of(WasteGasBalanceEnergyFlowDataSource.builder().energyFlowDataSourceNo("no").wasteGasActivityDetails(Map.of(WasteGasActivity.WASTE_GAS_PRODUCED,WasteGasBalanceEnergyFlowDataSourceDetails.builder().build())).build()))
                .dataSourcesMethodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build()).build();

        Set<String> messages = messages(validator.validate(wasteGasBalance1));
        messages.addAll(messages(validator.validate(wasteGasBalance2)));

        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_WasteGasBalance() {
        final WasteGasBalance wasteGasBalance1 = WasteGasBalance.builder()
                .wasteGasActivities(Collections.emptySet()).build();

        final WasteGasBalance wasteGasBalance2 = WasteGasBalance.builder()
                .wasteGasActivities(Set.of(WasteGasActivity.NO_WASTE_GAS_ACTIVITIES))
                .dataSources(List.of(WasteGasBalanceEnergyFlowDataSource.builder().energyFlowDataSourceNo("no").wasteGasActivityDetails(Map.of(WasteGasActivity.WASTE_GAS_CONSUMED,WasteGasBalanceEnergyFlowDataSourceDetails.builder().build())).build()))
                .dataSourcesMethodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build()).build();

        final WasteGasBalance wasteGasBalance3 = WasteGasBalance.builder()
                .wasteGasActivities(Set.of(WasteGasActivity.NO_WASTE_GAS_ACTIVITIES,WasteGasActivity.WASTE_GAS_FLARED))
                .dataSources(List.of(WasteGasBalanceEnergyFlowDataSource.builder().energyFlowDataSourceNo("no").wasteGasActivityDetails(Map.of(WasteGasActivity.WASTE_GAS_CONSUMED,WasteGasBalanceEnergyFlowDataSourceDetails.builder().build())).build()))
                .dataSourcesMethodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build()).build();


        Set<String> messages1 = messages(validator.validate(wasteGasBalance1));
        Set<String> messages2 = messages(validator.validate(wasteGasBalance2));
        Set<String> messages3 = messages(validator.validate(wasteGasBalance3));

        assertThat(messages1.size()).isEqualTo(3); //@SpELExpression 1 and 2 should contain empty check in order to work, even if @NotEmpty already exists
        assertThat(messages1).contains("must not be empty");

        assertThat(messages2.size()).isEqualTo(1);
        assertThat(messages2).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.wasteGasBalance.invalid_input_1}");

        assertThat(messages3.size()).isEqualTo(2);
        assertThat(messages3).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.wasteGasBalance.invalid_input_1}");
        assertThat(messages3).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.wasteGasBalance.invalid_input_2}");


    }
    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_SpecialProduct(){
        SubInstallation subInstallation1 = SubInstallation.builder().subInstallationType(SubInstallationType.REFINERY_PRODUCTS)
                .specialProduct(RefineryProductsSP.builder().build()).build();

        SubInstallation subInstallation2 = SubInstallation.builder().subInstallationType(SubInstallationType.AMMONIA)
                .specialProduct(null).build();

        Set<String> messages1 = messages(validator.validate(subInstallation1));
        Set<String> messages2 = messages(validator.validate(subInstallation2));

        assertFalse(messages1.contains("{permit.monitoringmethodologyplans.digitized.subinstallation.specialProduct.invalid_input_1}"));
        assertFalse(messages2.contains("{permit.monitoringmethodologyplans.digitized.subinstallation.specialProduct.invalid_input_1}"));

    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_SpecialProduct(){
        SubInstallation subInstallation1 = SubInstallation.builder().subInstallationType(SubInstallationType.REFINERY_PRODUCTS)
                .specialProduct(null).build();

        SubInstallation subInstallation2 = SubInstallation.builder().subInstallationType(SubInstallationType.AMMONIA)
                .specialProduct(RefineryProductsSP.builder().build()).build();

        Set<String> messages1 = messages(validator.validate(subInstallation1));
        Set<String> messages2 = messages(validator.validate(subInstallation2));

        assertThat(messages1).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.specialProduct.invalid_input_1}");
        assertThat(messages2).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.specialProduct.invalid_input_1}");

    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_SpecialProduct_RefineryProducts() {
        RefineryProductsSP refineryProductsSP = RefineryProductsSP.builder()
                .specialProductType(SpecialProductType.REFINERY_PRODUCTS)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .refineryProductsRelevantCWTFunctions(Set.of(AnnexIIPoint1.ASPHALT_MANUFACTURE, AnnexIIPoint1.AIR_SEPARATION))
                .refineryProductsDataSources(List.of(RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder().
                        dataSourceNo("1")
                        .details(Map.of(AnnexIIPoint1.ASPHALT_MANUFACTURE, AnnexVIISection44.INDIRECT_DETERMINATION)).build(),
                        RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder()
                                .dataSourceNo("2")
                                .details(new HashMap<>() {{  // Use a HashMap to allow null values
                                    put(AnnexIIPoint1.AIR_SEPARATION, null);
                                }}).build()))
                .build();

        Set<String> messages = messages(validator.validate(refineryProductsSP));

        assertThat(messages.size()).isEqualTo(0);

    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_SpecialProduct_RefineryProducts() {
        RefineryProductsSP refineryProductsSP = RefineryProductsSP.builder()
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .refineryProductsRelevantCWTFunctions(Set.of())
                .refineryProductsDataSources(List.of(RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder().
                                dataSourceNo("1")
                                .details(Map.of()).build())).build();

        Set<String> messages = messages(validator.validate(refineryProductsSP));
        assertThat(messages).contains("must not be empty");
        assertThat(messages).contains("must not be null");

        assertThat(messages.size()).isEqualTo(2);

    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_SpecialProduct_Lime() {
        LimeSP limeSP = LimeSP.builder()
                .specialProductType(SpecialProductType.LIME)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(List.of(LimeDataSource.builder().dataSourceNo("1").detail(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN).build()))
                .build();

        Set<String> messages = messages(validator.validate(limeSP));

        assertThat(messages.size()).isEqualTo(0);

    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_SpecialProduct_Lime() {
        LimeSP limeSP = LimeSP.builder()
                .specialProductType(SpecialProductType.LIME)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(new ArrayList<>())
                .build();

        Set<String> messages = messages(validator.validate(limeSP));

        assertThat(messages).contains("size must be between 1 and 6");
        assertThat(messages.size()).isEqualTo(1);

    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_SpecialProduct_Dolime() {
        DolimeSP dolimeSP = DolimeSP.builder()
                .specialProductType(SpecialProductType.DOLIME)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(List.of(DolimeDataSource.builder().dataSourceNo("1").detail(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN).build()))
                .build();

        Set<String> messages = messages(validator.validate(dolimeSP));

        assertThat(messages.size()).isEqualTo(0);

    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_SpecialProduct_Dolime() {
        DolimeSP dolimeSP = DolimeSP.builder()
                .specialProductType(SpecialProductType.DOLIME)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(new ArrayList<>())
                .build();

        Set<String> messages = messages(validator.validate(dolimeSP));

        assertThat(messages).contains("size must be between 1 and 6");
        assertThat(messages.size()).isEqualTo(1);

    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_SpecialProduct_SteamCracking() {
        SteamCrackingSP limeSP = SteamCrackingSP.builder()
                .specialProductType(SpecialProductType.STEAM_CRACKING)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(List.of(SteamCrackingDataSource.builder().dataSourceNo("1").detail(AnnexVIISection44.METHOD_MONITORING_PLAN).build()))
                .build();

        Set<String> messages = messages(validator.validate(limeSP));

        assertThat(messages.size()).isEqualTo(0);

    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_SpecialProduct_SteamCracking() {
        SteamCrackingSP steamCrackingSPSP = SteamCrackingSP.builder()
                .specialProductType(SpecialProductType.STEAM_CRACKING)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(new ArrayList<>())
                .build();

        Set<String> messages = messages(validator.validate(steamCrackingSPSP));

        assertThat(messages).contains("size must be between 1 and 6");
        assertThat(messages.size()).isEqualTo(1);

    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_SpecialProduct_Aromatics() {
        AromaticsSP aromaticsSP = AromaticsSP.builder()
                .specialProductType(SpecialProductType.AROMATICS)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .relevantCWTFunctions(Set.of(AnnexIIPoint2.HYDRODEALKYLATION))
                .dataSources(List.of(AromaticsQuantitiesOfSupplementalFieldDataSource.builder().
                                dataSourceNo("1")
                                .details(Map.of(AnnexIIPoint2.HYDRODEALKYLATION, AnnexVIISection44.INDIRECT_DETERMINATION)).build()
                        ))
                .build();

        Set<String> messages = messages(validator.validate(aromaticsSP));

        assertThat(messages.size()).isEqualTo(0);

    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_SpecialProduct_Aromatics() {
        AromaticsSP aromaticsSP = AromaticsSP.builder()
                .specialProductType(SpecialProductType.AROMATICS)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .relevantCWTFunctions(new HashSet<>())
                .dataSources(List.of(AromaticsQuantitiesOfSupplementalFieldDataSource.builder().
                        dataSourceNo("1")
                        .details(Map.of()).build()
                ))
                .build();

        Set<String> messages = messages(validator.validate(aromaticsSP));
        assertThat(messages).contains("must not be empty");

        assertThat(messages.size()).isEqualTo(1);

    }


    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_AnnualActivityHeatLevel() {
        final AnnualActivityHeatLevel annualActivityHeatLevel = AnnualActivityHeatLevel.builder()
                .annualLevelType(AnnualLevelType.ACTIVITY_HEAT)
                .measurableHeatFlowList(List.of(MeasurableHeatFlow.builder().measurableHeatFlowQuantificationNo("1").quantification(AnnexVIISection45.LEGAL_METROLOGICAL_CONTROL_READING).net(AnnexVIISection72.MEASUREMENTS).build()))
                .methodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .trackingMethodologyDescription("description").build();
        Set<String> messages = messages(validator.validate(annualActivityHeatLevel));
        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_AnnualActivityHeatLevel() {
        final AnnualActivityHeatLevel annualActivityHeatLevel = AnnualActivityHeatLevel.builder()
                .annualLevelType(null)
                .measurableHeatFlowList(new ArrayList<>())
                .methodologyAppliedDescription(null)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .trackingMethodologyDescription("description").build();

        Set<String> messages = messages(validator.validate(annualActivityHeatLevel));

        assertThat(messages.size()).isEqualTo(4);
        assertThat(messages).contains("size must be between 1 and 6");
        assertThat(messages).contains("must not be blank");
        assertThat(messages).contains("must not be null");
        assertThat(messages).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.annuallevel.invalid_input_hierarchical_order}");
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_AnnualActivityFuelLevel() {
        final AnnualActivityFuelLevel annualActivityFuelLevel = AnnualActivityFuelLevel.builder()
                .annualLevelType(AnnualLevelType.ACTIVITY_FUEL)
                .fuelDataSources(List.of(ActivityFuelDataSource.builder().fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                        .dataSourceQuantificationNumber("1").energyContent(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61).build()))
                .methodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .trackingMethodologyDescription("description").build();
        Set<String> messages = messages(validator.validate(annualActivityFuelLevel));
        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_AnnualActivityFuelLevel() {
        final AnnualActivityFuelLevel annualActivityFuelLevel = AnnualActivityFuelLevel.builder()
                .annualLevelType(null)
                .fuelDataSources(new ArrayList<>())
                .methodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .trackingMethodologyDescription("description").build();
        Set<String> messages = messages(validator.validate(annualActivityFuelLevel));
        assertThat(messages.size()).isEqualTo(3);
        assertThat(messages).contains("size must be between 1 and 6");
        assertThat(messages).contains("must not be null");
        assertThat(messages).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.annuallevel.invalid_input_hierarchical_order}");
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_AnnualActivityProcessLevel() {
        final AnnualActivityFuelLevel annualActivityFuelLevel = AnnualActivityFuelLevel.builder()
                .annualLevelType(AnnualLevelType.ACTIVITY_PROCESS)
                .methodologyAppliedDescription("description")
                .hierarchicalOrder(null)
                .trackingMethodologyDescription("description").build();
        Set<String> messages = messages(validator.validate(annualActivityFuelLevel));
        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_AnnualActivityProcessLevel() {
        final AnnualActivityFuelLevel annualActivityFuelLevel = AnnualActivityFuelLevel.builder()
                .annualLevelType(null)
                .methodologyAppliedDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .trackingMethodologyDescription("description").build();
        Set<String> messages = messages(validator.validate(annualActivityFuelLevel));
        assertThat(messages.size()).isEqualTo(2);
        assertThat(messages).contains("must not be null");
        assertThat(messages).contains("{permit.monitoringmethodologyplans.digitized.subinstallation.annuallevel.invalid_input_hierarchical_order}");
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_SpecialProduct_Hydrogen() {
        HydrogenSP hydrogenSP = HydrogenSP.builder()
                .specialProductType(SpecialProductType.HYDROGEN)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(List.of(HydrogenDataSource.builder().
                                dataSourceNo("1")
                                .details(HydrogenDetails.builder()
                                        .totalProduction(AnnexVIISection44.INDIRECT_DETERMINATION)
                                        .volumeFraction(AnnexVIISection46.SIMPLIFIED_LABORATORY_ANALYSES_SECTION_62)
                                        .build()).build()
                        ))
                .build();

        Set<String> messages = messages(validator.validate(hydrogenSP));

        assertThat(messages.size()).isEqualTo(0);

    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_SpecialProduct_Hydrogen() {
        HydrogenSP hydrogenSP = HydrogenSP.builder()
                .specialProductType(SpecialProductType.HYDROGEN)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(List.of(HydrogenDataSource.builder().
                        dataSourceNo("1").details(null).build()
                ))
                .build();

        Set<String> messages = messages(validator.validate(hydrogenSP));
        assertThat(messages).contains("must not be null");
        assertThat(messages.size()).isEqualTo(1);
    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_SpecialProduct_Synthesis_Gas() {
        SynthesisGasSP synthesisGasSP = SynthesisGasSP.builder()
                .specialProductType(SpecialProductType.SYNTHESIS_GAS)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(List.of(SynthesisGasDataSource.builder().
                        dataSourceNo("1").details(null).build()
                ))
                .build();

        Set<String> messages = messages(validator.validate(synthesisGasSP));
        assertThat(messages).contains("must not be null");
        assertThat(messages.size()).isEqualTo(1);


    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_SpecialProduct_EthyleneOxideEthyleneGlycols() {
        EthyleneOxideEthyleneGlycolsSP ethyleneOxideEthyleneGlycolsSP = EthyleneOxideEthyleneGlycolsSP.builder()
                .specialProductType(SpecialProductType.ETHYLENE_OXIDE_ETHYLENE_GLYCOLS)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(List.of(EthyleneOxideEthyleneGlycolsDataSource.builder().
                        dataSourceNo("1")
                        .details(EthyleneOxideEthyleneGlycolsDetails.builder()
                                .build()).build()
                ))
                .build();

        Set<String> messages = messages(validator.validate(ethyleneOxideEthyleneGlycolsSP));

        assertThat(messages.size()).isEqualTo(0);

    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_SpecialProduct_EthyleneOxideEthyleneGlycols() {
        EthyleneOxideEthyleneGlycolsSP ethyleneOxideEthyleneGlycolsSP1 = EthyleneOxideEthyleneGlycolsSP.builder()
                .specialProductType(SpecialProductType.ETHYLENE_OXIDE_ETHYLENE_GLYCOLS)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(new ArrayList<>())
                .build();

        Set<String> messages1 = messages(validator.validate(ethyleneOxideEthyleneGlycolsSP1));
        assertThat(messages1).contains("size must be between 1 and 6");
        assertThat(messages1.size()).isEqualTo(1);

        EthyleneOxideEthyleneGlycolsSP ethyleneOxideEthyleneGlycolsSP2 = EthyleneOxideEthyleneGlycolsSP.builder()
                .specialProductType(SpecialProductType.ETHYLENE_OXIDE_ETHYLENE_GLYCOLS)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(List.of(EthyleneOxideEthyleneGlycolsDataSource.builder().
                        dataSourceNo("")
                        .details(null).build()
                ))
                .build();

        Set<String> messages2 = messages(validator.validate(ethyleneOxideEthyleneGlycolsSP2));
        assertThat(messages2).contains("must not be blank");
        assertThat(messages2).contains("must not be null");
        assertThat(messages2.size()).isEqualTo(2);
    }

    @Test
    public void validationReturnsNoErrorMessages_SubInstallation_SpecialProduct_VinylChlorideMonomer() {
        VinylChlorideMonomerSP vinylChlorideMonomerSP = VinylChlorideMonomerSP.builder()
                .specialProductType(SpecialProductType.VINYL_CHLORIDE_MONOMER)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(List.of(VinylChlorideMonomerDataSource.builder().dataSourceNo("1").detail(AnnexVIISection45.OTHER_METHODS).build()))
                .build();

        Set<String> messages = messages(validator.validate(vinylChlorideMonomerSP));

        assertThat(messages.size()).isEqualTo(0);

    }

    @Test
    public void validationReturnsErrorMessages_SubInstallation_SpecialProduct_VinylChlorideMonomer() {
        VinylChlorideMonomerSP vinylChlorideMonomerSP = VinylChlorideMonomerSP.builder()
                .specialProductType(SpecialProductType.VINYL_CHLORIDE_MONOMER)
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .methodologyAppliedDescription("description")
                .dataSources(new ArrayList<>())
                .build();

        Set<String> messages = messages(validator.validate(vinylChlorideMonomerSP));

        assertThat(messages).contains("size must be between 1 and 6");
        assertThat(messages.size()).isEqualTo(1);

    }

    @Test
    public void valid_FallbackApproach_HeatFuelInputAndRelevantEmissionFactor() {
        FuelInputAndRelevantEmissionFactorHeatFA fuelInputAndRelevantEmissionFactorHeatFAFirst =
                FuelInputAndRelevantEmissionFactorHeatFA.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.HEAT_FALLBACK_APPROACH)
                        .exists(true).wasteGasesInput(false).hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                        .methodologyAppliedDescription("desc")
                        .dataSources(List.of(FuelInputDataSourceFA.builder().fuelInputDataSourceNo("0")
                                .weightedEmissionFactor(AnnexVIISection46.CONSTANT_VALUES_SCIENTIFIC_EVIDENCE)
                                .fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                                .netCalorificValue(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61).build())).build();
        FuelInputAndRelevantEmissionFactorHeatFA fuelInputAndRelevantEmissionFactorHeatFASecond = FuelInputAndRelevantEmissionFactorHeatFA.builder()
                .fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.HEAT_FALLBACK_APPROACH).exists(false).build();
        assertTrue(validator.validate(fuelInputAndRelevantEmissionFactorHeatFAFirst).isEmpty());
        assertTrue(validator.validate(fuelInputAndRelevantEmissionFactorHeatFASecond).isEmpty());
    }

    @Test
    public void invalid_FallbackApproach_HeatFuelInputAndRelevantEmissionFactor() {
        FuelInputAndRelevantEmissionFactorHeatFA fuelInputAndRelevantEmissionFactorHeatFAFirst =
                FuelInputAndRelevantEmissionFactorHeatFA.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.HEAT_FALLBACK_APPROACH)
                        .wasteGasesInput(false).hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                        .methodologyAppliedDescription("desc")
                        .dataSources(List.of(FuelInputDataSourceFA.builder().fuelInputDataSourceNo("0")
                                .weightedEmissionFactor(AnnexVIISection46.CONSTANT_VALUES_SCIENTIFIC_EVIDENCE)
                                .fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                                .netCalorificValue(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61).build())).build();
        FuelInputAndRelevantEmissionFactorHeatFA fuelInputAndRelevantEmissionFactorHeatFASecond = FuelInputAndRelevantEmissionFactorHeatFA.builder()
                .fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.HEAT_FALLBACK_APPROACH).exists(true).build();
        assertFalse(validator.validate(fuelInputAndRelevantEmissionFactorHeatFAFirst).isEmpty());
        assertFalse(validator.validate(fuelInputAndRelevantEmissionFactorHeatFASecond).isEmpty());
    }

    @Test
    public void test_getDigitizedPlanAttachmentIds() {
        Set<UUID> attachmentIds = Set.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID());

        List<SubInstallation> subInstallations = List.of(
                SubInstallation.builder()
                        .supportingFiles(Set.of(attachmentIds.stream().skip(1).findFirst().get()))
                        .annualLevel(AnnualProductionLevel.builder().supportingFiles(Set.of(attachmentIds.stream().skip(2).findFirst().get())).build())
                        .fuelAndElectricityExchangeability(FuelAndElectricityExchangeability.builder().supportingFiles(Set.of(attachmentIds.stream().skip(3).findFirst().get())).build())
                        .importedMeasurableHeatFlow(ImportedMeasurableHeatFlow.builder().exist(true).supportingFiles(Set.of(attachmentIds.stream().skip(4).findFirst().get())).build())
                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().supportingFiles(Set.of(attachmentIds.stream().skip(5).findFirst().get())).build())
                        .fuelInputAndRelevantEmissionFactor(FuelInputAndRelevantEmissionFactorPB.builder().exist(true).supportingFiles(Set.of(attachmentIds.stream().skip(6).findFirst().get())).build())
                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder().supportingFiles(Set.of(attachmentIds.stream().skip(7).findFirst().get())).build())
                        .wasteGasBalance(WasteGasBalance.builder().supportingFiles(Set.of(attachmentIds.stream().skip(8).findFirst().get())).build())
                        .specialProduct(RefineryProductsSP.builder().supportingFiles(Set.of(attachmentIds.stream().skip(9).findFirst().get())).build())
                        .build()
        );

        DigitizedPlan digitizedPlan = DigitizedPlan.builder()
                .installationDescription(DigitizedMmpInstallationDescription.builder().flowDiagrams(Set.of(attachmentIds.iterator().next())).build())
                .subInstallations(subInstallations)
                .build();

        assertThat(digitizedPlan.getAttachmentIds()).isEqualTo(attachmentIds);
    }

    @Test
    void test_measurableHeatFieldIncludedForSpecificSubInstallationType() throws IOException {
        final JacksonTester<SubInstallation> json = new JacksonTester<>(this.getClass(), ResolvableType.forClass(SubInstallation.class), new ObjectMapper());

        SubInstallation subInstallation = SubInstallation.builder()
                .subInstallationNo("1")
                .subInstallationType(SubInstallationType.HEAT_BENCHMARK_CL)
                .description("Test Description")
                .measurableHeat(MeasurableHeat.builder().measurableHeatProduced(MeasurableHeatProduced.builder().build()).build())
                .build();

        JsonContent<SubInstallation> result = json.write(subInstallation);
        assertThat(result).extractingJsonPathValue("$.measurableHeat").isNotNull();
    }

    @Test
    void test_measurableHeatFieldNotIncludedForSubInstallationType() throws IOException {
        final JacksonTester<SubInstallation> json = new JacksonTester<>(this.getClass(), ResolvableType.forClass(SubInstallation.class), new ObjectMapper());

        SubInstallation subInstallation = SubInstallation.builder()
                .subInstallationNo("1")
                .subInstallationType(SubInstallationType.AROMATICS)
                .description("Test Description")
                .build();

        JsonContent<SubInstallation> result = json.write(subInstallation);
        assertThat(result).doesNotHaveJsonPath("$.measurableHeat");
    }

    @Test
    void test_measurableHeatProducedFieldJsonMapping() throws IOException {
        SubInstallation subInstallation = getSubInstallationFromFile("subInstallation_MeasurableHeat");
        assertEquals(SubInstallationType.HEAT_BENCHMARK_CL, subInstallation.getSubInstallationType());
        assertNotNull(subInstallation.getMeasurableHeat());
        assertNotNull(subInstallation.getMeasurableHeat().getMeasurableHeatProduced());
        assertEquals(1, subInstallation.getMeasurableHeat().getMeasurableHeatProduced().getDataSources().size());
        assertEquals(AnnexVIISection45.LEGAL_METROLOGICAL_CONTROL_READING, subInstallation.getMeasurableHeat().getMeasurableHeatProduced().getDataSources().getFirst().getHeatProduced());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_validMeasurableHeatProduced() throws IOException {

        SubInstallation subInstallation = getSubInstallationFromFile("subInstallation_MeasurableHeat");
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        PermitContainer permitContainer = PermitContainer.builder().installationOperatorDetails(InstallationOperatorDetails.builder().emitterId("test").build()).
                permit(Permit.builder().monitoringMethodologyPlans(MonitoringMethodologyPlans.builder()
                        .exist(true).digitizedPlan(DigitizedPlan.builder()
                                .subInstallations(List.of(subInstallation)).methodTask(MethodTask.builder().build()).build()).build()).build()).build();
        when(dataSourceValidatorFactory.getValidator(MeasurableHeatImported.class)).thenReturn(measurableHeatImportedDataSourceValidator);
        when(measurableHeatImportedDataSourceValidator.validateDataSources(permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getMeasurableHeat().getMeasurableHeatImported())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(AnnualActivityLevel.class)).thenReturn(annualActivityLevelDataSourceValidator);
        when(annualActivityLevelDataSourceValidator.validateDataSources((AnnualActivityHeatLevel) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getAnnualLevel())).thenReturn(true);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        assertTrue(validator.validate(subInstallation.getMeasurableHeat().getMeasurableHeatProduced()).isEmpty());
        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }


    @Test
    public void test_DigitizedMmpSectionValidator_InvalidMeasurableHeatProduced() throws IOException {

        SubInstallation subInstallation = getSubInstallationFromFile("subInstallation_InvalidMeasurableHeat");
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        PermitContainer permitContainer = PermitContainer.builder().installationOperatorDetails(InstallationOperatorDetails.builder().emitterId("test").build()).
                permit(Permit.builder().monitoringMethodologyPlans(MonitoringMethodologyPlans.builder()
                        .exist(true).digitizedPlan(DigitizedPlan.builder()
                                .methodTask(MethodTask.builder().build())
                                .subInstallations(List.of(subInstallation)).build()).build()).build()).build();
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        assertFalse(validator.validate(subInstallation.getMeasurableHeat().getMeasurableHeatProduced()).isEmpty());
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }

    @Test
    public void test_DigitizedMmpSectionValidator_validMeasurableHeatImported() throws IOException {

        SubInstallation subInstallation = getSubInstallationFromFile("subInstallation_MeasurableHeatImported");
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        PermitContainer permitContainer = PermitContainer.builder().installationOperatorDetails(InstallationOperatorDetails.builder().emitterId("test").build()).
                permit(Permit.builder().monitoringMethodologyPlans(MonitoringMethodologyPlans.builder()
                        .exist(true).digitizedPlan(DigitizedPlan.builder()
                                .subInstallations(List.of(subInstallation)).methodTask(MethodTask.builder().build()).build()).build()).build()).build();
        when(dataSourceValidatorFactory.getValidator(MeasurableHeatImported.class)).thenReturn(measurableHeatImportedDataSourceValidator);
        when(measurableHeatImportedDataSourceValidator.validateDataSources(permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getMeasurableHeat().getMeasurableHeatImported())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(AnnualActivityLevel.class)).thenReturn(annualActivityLevelDataSourceValidator);
        when(annualActivityLevelDataSourceValidator.validateDataSources((AnnualActivityHeatLevel) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getAnnualLevel())).thenReturn(true);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }


    @Test
    public void test_DigitizedMmpSectionValidator_InvalidMeasurableHeatImported() throws IOException {

        SubInstallation subInstallation = getSubInstallationFromFile("subInstallation_InvalidMeasurableHeatImported");
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        PermitContainer permitContainer = PermitContainer.builder().installationOperatorDetails(InstallationOperatorDetails.builder().emitterId("test").build()).
                permit(Permit.builder().monitoringMethodologyPlans(MonitoringMethodologyPlans.builder()
                        .exist(true).digitizedPlan(DigitizedPlan.builder().methodTask(
                                MethodTask.builder().build()
                        ).subInstallations(List.of(subInstallation)).build()).build()).build()).build();
        when(dataSourceValidatorFactory.getValidator(AnnualActivityLevel.class)).thenReturn(annualActivityLevelDataSourceValidator);
        when(annualActivityLevelDataSourceValidator.validateDataSources((AnnualActivityHeatLevel) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getAnnualLevel())).thenReturn(true);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        assertFalse(digitizedMmpSectionValidator.validate(permitContainer).isValid());
    }


    @Test
    public void test_DigitizedMmpSectionValidator_validMeasurableHeatExported() throws IOException {
        SubInstallation subInstallation = getSubInstallationFromFile("subInstallation_MeasurableHeatExported");
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(any())).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        PermitContainer permitContainer = PermitContainer.builder().installationOperatorDetails(InstallationOperatorDetails.builder().emitterId("test").build()).
                permit(Permit.builder().monitoringMethodologyPlans(MonitoringMethodologyPlans.builder()
                        .exist(true).digitizedPlan(DigitizedPlan.builder().subInstallations(List.of(subInstallation)).methodTask(MethodTask.builder().build()).build()).build()).build()).build();
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(AnnualActivityLevel.class)).thenReturn(annualActivityLevelDataSourceValidator);
        when(annualActivityLevelDataSourceValidator.validateDataSources((AnnualActivityFuelLevel) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getAnnualLevel())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(MeasurableHeatExported.class)).thenReturn(measurableHeatExportedDataSourceValidator);
        when(measurableHeatExportedDataSourceValidator.validateDataSources(permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getMeasurableHeat().getMeasurableHeatExported())).thenReturn(true);
        assertTrue(digitizedMmpSectionValidator.validate(permitContainer).isValid());

    }

    @Test
    public void test_existingPhysicalPartAndUnitsGuard_with2subInstallationToBeInvalid() {

        String emitterId = "emitter_id";
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorPB.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK).exist(false).build();
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.SCOTLAND);
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder().subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.E_PVC)
                                                        .subInstallationNo("not blank value")
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                        .build(),
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.E_PVC)
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .subInstallationNo("not blank value")
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                        .build()
                                        )
                                ).methodTask(MethodTask.builder().build()).build())
                                .build()).build()).build();
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        PermitValidationResult validationResult = digitizedMmpSectionValidator.validate(permitContainer);
        assertFalse(validationResult.isValid());
    }

    @Test
    public void test_existingNotValidMissingAmmoniaFromConnections() {

        String emitterId = "emitter_id";
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorPB.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK).exist(false).build();
        when(configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)).thenReturn(DIGITIZED_CONFIG_RESULT);
        when(accountRepository.findCompetentAuthorityByEmitterId(emitterId)).thenReturn(CompetentAuthorityEnum.SCOTLAND);
        PermitContainer permitContainer = PermitContainer.builder()
                .installationOperatorDetails(InstallationOperatorDetails.builder().emitterId(emitterId).build())
                .permit(Permit.builder()
                        .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                                .digitizedPlan(DigitizedPlan.builder().subInstallations(
                                        List.of(
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.E_PVC)
                                                        .subInstallationNo("not blank value")
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                        .build(),
                                                SubInstallation.builder()
                                                        .subInstallationType(SubInstallationType.EAF_CARBON_STEEL)
                                                        .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder().directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                                        .subInstallationNo("not blank value")
                                                        .importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                                                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                                        .annualLevel(getGenericAnnualLevel(AnnualLevelType.PRODUCTION))
                                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                                        .build()
                                        )
                                ).methodTask(MethodTask.builder().physicalPartsAndUnitsAnswer(true).connections(
                                        List.of(
                                                MethodTaskConnection.builder().subInstallations(List.of(SubInstallationType.E_PVC, SubInstallationType.AMMONIA)).build()
                                        )
                                ).build()).build())
                                .build()).build()).build();
        when(dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)).thenReturn(fuelInputAndRelevantEmissionFactorPBDatasourceValidator);
        when(fuelInputAndRelevantEmissionFactorPBDatasourceValidator.validateDataSources((FuelInputAndRelevantEmissionFactorPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getFuelInputAndRelevantEmissionFactor())).thenReturn(true);
        when(dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class)).thenReturn(directlyAttributableEmissionsPBDataSourceValidator);
        when(directlyAttributableEmissionsPBDataSourceValidator.validateDataSources((DirectlyAttributableEmissionsPB) permitContainer.getPermit().getMonitoringMethodologyPlans().getDigitizedPlan().getSubInstallations().getFirst().getDirectlyAttributableEmissions())).thenReturn(true);
        PermitValidationResult validationResult = digitizedMmpSectionValidator.validate(permitContainer);
        assertFalse(validationResult.isValid());
        assertTrue(((String) validationResult.getPermitViolations().get(0).getData()[0])
                .contains("Missing AMMONIA from sub installations of digital plan"));
    }

    private Set<String> messages(Set<ConstraintViolation<Object>> constraintViolations) {
        return constraintViolations
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }

    private PermitContainer getAnnualLevelPermitContainer(SubInstallationType subInstallationType,AnnualLevelType annualLevelType) {
        AnnualLevel annualLevel;
        switch (annualLevelType) {
            case PRODUCTION -> {
                annualLevel = AnnualProductionLevel.builder().annualLevelType(annualLevelType).supportingFiles(Set.of()).build();
            }
            case ACTIVITY_HEAT -> {
                annualLevel = AnnualActivityHeatLevel.builder().annualLevelType(annualLevelType).supportingFiles(Set.of()).build();
            }
            case ACTIVITY_FUEL -> {
                annualLevel = AnnualActivityFuelLevel.builder().annualLevelType(annualLevelType).supportingFiles(Set.of()).build();
            }
            case ACTIVITY_PROCESS -> {
                annualLevel = AnnualActivityProcessLevel.builder().annualLevelType(annualLevelType).supportingFiles(Set.of()).build();
            }
            default -> throw new IllegalStateException("Unexpected value: " + annualLevelType);
        }

        DirectlyAttributableEmissions directlyAttributableEmissions;
        switch (annualLevelType) {
            case PRODUCTION -> directlyAttributableEmissions = DirectlyAttributableEmissionsPB.builder()
                    .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK)
                    .attribution("attribution")
                    .furtherInternalSourceStreamsRelevant(false)
                    .transferredCO2ImportedOrExportedRelevant(false)
                    .build();
            case ACTIVITY_HEAT, ACTIVITY_FUEL -> directlyAttributableEmissions = DirectlyAttributableEmissionsFA.builder()
                    .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.FALLBACK_APPROACH)
                    .attribution("attribution")
                    .build();
            case ACTIVITY_PROCESS -> directlyAttributableEmissions = null;
            default -> throw new IllegalStateException("Unexpected value: " + annualLevelType);
        }

        FuelInputAndRelevantEmissionFactor fuelInputAndRelevantEmissionFactor;
        if (FuelInputAndRelevantEmissionFactor.getEmptyFIandREF_SubInstallationTypes().contains(subInstallationType))
            fuelInputAndRelevantEmissionFactor = null;
        else {
            if (Objects.equals(subInstallationType.getCategory(), SubInstallationCategory.PRODUCT_BENCHMARK)) {
                fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorPB.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.PRODUCT_BENCHMARK).exist(false).build();
            }
            else {
                fuelInputAndRelevantEmissionFactor = FuelInputAndRelevantEmissionFactorFA.builder().fuelInputAndRelevantEmissionFactorType(FuelInputAndRelevantEmissionFactorType.FALLBACK_APPROACH).wasteGasesInput(false).build();
            }
        }

        MeasurableHeat measurableHeat;
        if (Objects.equals(subInstallationType.getCategory(), SubInstallationCategory.PRODUCT_BENCHMARK) ||
                Objects.equals(subInstallationType,SubInstallationType.PROCESS_EMISSIONS_CL) ||
                Objects.equals(subInstallationType,SubInstallationType.PROCESS_EMISSIONS_NON_CL)) {
            measurableHeat = null;
        }
        else {
            measurableHeat = MeasurableHeat.builder().build();
            if (MeasurableHeat.getMeasurableHeatProducedSupportingSubInstallationTypes().contains(subInstallationType)) {
                measurableHeat.setMeasurableHeatProduced(MeasurableHeatProduced.builder().build());
            }
            if (MeasurableHeat.getMeasurableHeatImportedSupportingSubInstallationTypes().contains(subInstallationType)) {
                measurableHeat.setMeasurableHeatImported(MeasurableHeatImported.builder().measurableHeatImportedActivities(Set.of(MeasurableHeatImportedType.MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED)).build());
            }
            if (MeasurableHeat.getMeasurableHeatExportedSupportingSubInstallationTypes().contains(subInstallationType)) {
                measurableHeat.setMeasurableHeatExported(MeasurableHeatExported.builder().measurableHeatExported(false).build());
            }
        }


        final ImportedExportedMeasurableHeat importedExportedMeasurableHeat = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build();

        return PermitContainer.builder().installationOperatorDetails(InstallationOperatorDetails.builder().emitterId("test").build()).
                permit(Permit.builder().monitoringMethodologyPlans(MonitoringMethodologyPlans.builder()
                        .exist(true).digitizedPlan(DigitizedPlan.builder().methodTask(MethodTask.builder().avoidDoubleCountToggle(true).build()).subInstallations(List.of(
                                SubInstallation.builder().subInstallationType(subInstallationType)
                                        .directlyAttributableEmissions(directlyAttributableEmissions)
                                        .importedExportedMeasurableHeat(importedExportedMeasurableHeat)
                                        .measurableHeat(measurableHeat)
                                        .fuelInputAndRelevantEmissionFactor(fuelInputAndRelevantEmissionFactor)
                                        .annualLevel(annualLevel).build()
                        )).build()).build()).build()).build();
    }

    private PermitContainer getSpecialProductPermitContainer(SubInstallationType subInstallationType, SpecialProduct specialProduct) {
        return PermitContainer.builder().installationOperatorDetails(InstallationOperatorDetails.builder().emitterId("test").build())
                .permit(Permit.builder().monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().exist(true)
                        .digitizedPlan(DigitizedPlan.builder().subInstallations(List.of(SubInstallation.builder()
                                .subInstallationType(subInstallationType).importedExportedMeasurableHeat(ImportedExportedMeasurableHeat.builder()
                                        .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)).build())
                                .fuelAndElectricityExchangeability(FuelAndElectricityExchangeability.builder().build())
                                .directlyAttributableEmissions(DirectlyAttributableEmissionsPB.builder()
                                        .directlyAttributableEmissionsType(DirectlyAttributableEmissionsType.PRODUCT_BENCHMARK).build())
                                .annualLevel(AnnualProductionLevel.builder().annualLevelType(AnnualLevelType.PRODUCTION)
                                        .supportingFiles(Set.of()).build()).specialProduct(specialProduct).build())).build()).build()).build())
                .build();
    }

    private AnnualLevel getGenericAnnualLevel(AnnualLevelType annualLevelType) {
        return AnnualProductionLevel.builder().annualLevelType(annualLevelType).build();
    }

    private SubInstallation getSubInstallationFromFile(String fileName) throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get(String.format("src/test/resources/files/mmp/%s.json",fileName))));
        return new ObjectMapper().readValue(jsonContent, SubInstallation.class);
    }

    private Map<ProcedureType, Procedure> getProceduresFromFile(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = new String(Files.readAllBytes(Paths.get(String.format("src/test/resources/files/mmp/%s.json",fileName))));
        return objectMapper.readValue(
                jsonContent.getBytes(),
                objectMapper.getTypeFactory().constructMapType(
                        Map.class,
                        ProcedureType.class,
                        Procedure.class
                )
        );
    }
}
