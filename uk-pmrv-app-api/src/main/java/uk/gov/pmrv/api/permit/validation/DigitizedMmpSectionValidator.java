package uk.gov.pmrv.api.permit.validation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.MonitoringMethodologyPlans;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.EnergyFlows;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.FuelInputDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.MeasurableHeatFlowsDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.WasteGasFlowsDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.methodtasks.MethodTask;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationCategory;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.AnnualLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityFuelLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityHeatLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityProcessLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels.AnnualProductionLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsFA;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorFA;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorHeatFA;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeat;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeatEnergyFlowDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeatType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.MeasurableHeat;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported.MeasurableHeatExported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.SpecialProduct;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics.AromaticsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols.EthyleneOxideEthyleneGlycolsDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols.EthyleneOxideEthyleneGlycolsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts.RefineryProductsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasActivity;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasBalanceEnergyFlowDataSourceDetails;
import uk.gov.pmrv.api.permit.validation.datasourceValidation.DataSourceValidatorFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Optional;

import java.util.stream.Stream;

import static uk.gov.pmrv.api.permit.validation.utils.DigitizedMmpSectionValidatorUtils.*;

@Component
@RequiredArgsConstructor
public class DigitizedMmpSectionValidator implements PermitContextValidator, PermitGrantedContextValidator {

    private final ConfigurationService configurationService;

    private final AccountRepository accountRepository;
    private final DataSourceValidatorFactory dataSourceValidatorFactory;

    // TODO: will be removed when we fully move to mmp digitized version and keep only digitized version validation part
    @Override
    public PermitValidationResult validate(@Valid PermitContainer permitContainer) {
        PermitValidationResult valid = PermitValidationResult.builder().valid(true).build();

        MonitoringMethodologyPlans monitoringMethodologyPlans = permitContainer.getPermit().getMonitoringMethodologyPlans();
        boolean isDigitizedVersion = configurationService.getConfigurationByKey(DIGITIZED_MMP_FLAG_CONFIG_KEY)
            .map(ConfigurationDTO::getValue).filter(Boolean.class::isInstance).map(Boolean.class::cast)
            .orElse(false);
        boolean hasMmp = monitoringMethodologyPlans.isExist();
        boolean hasPlans = !ObjectUtils.isEmpty(monitoringMethodologyPlans.getPlans());
        boolean hasDigitizedPlan = !ObjectUtils.isEmpty(monitoringMethodologyPlans.getDigitizedPlan());

        if (!hasMmp && !hasPlans && !hasDigitizedPlan) {
            return valid;
        } else if (hasMmp && !isDigitizedVersion && hasPlans && !hasDigitizedPlan) {
            return valid;
        } else if (hasMmp && isDigitizedVersion && hasDigitizedPlan) {
            return this.validateDigitizedVersion(permitContainer);
        }
        //This is done on purpose as we will have the new digitized version working in parallel (at least for some period) with the old version
        else if (hasMmp && isDigitizedVersion && (hasPlans)) {
            return valid;
        }

        return PermitValidationResult.invalidPermit(
            List.of(new PermitViolation(PermitViolation.PermitViolationMessage.PERMIT_MMP_VERSION_NOT_COMPLIANT,
                monitoringMethodologyPlans)));
    }

    private PermitValidationResult validateDigitizedVersion(PermitContainer permitContainer) {
        String currentEmitterId = permitContainer.getInstallationOperatorDetails().getEmitterId();
        CompetentAuthorityEnum operatorAccountCompetentAuthority = accountRepository.findCompetentAuthorityByEmitterId(currentEmitterId);
        MonitoringMethodologyPlans mmp = permitContainer.getPermit().getMonitoringMethodologyPlans();
        DigitizedPlan digitizedPlan = mmp.getDigitizedPlan();
        List<String> violationErrors = new ArrayList<>();

        List<SubInstallation> subInstallations = digitizedPlan.getSubInstallations();
        if(subInstallations.isEmpty()) {
            violationErrors.add(ERROR_NO_SUB_INSTALLATIONS);
        }

        if (Objects.equals(CompetentAuthorityEnum.OPRED, operatorAccountCompetentAuthority)) {
            validateOpredInstallation(subInstallations,violationErrors);
        }

        if(!validateSubInstallationTypeAndSize(subInstallations)) {
            violationErrors.add(ERROR_INVALID_SUB_INSTALLATION_SIZE);
        }

        Set<SubInstallationType> typeSet = new HashSet<>();
        boolean hasDuplicateTypes = subInstallations.stream()
                .map(SubInstallation::getSubInstallationType)
                .anyMatch(type -> !typeSet.add(type));
        if (hasDuplicateTypes) {
            violationErrors.add(ERROR_DUPLICATE_SUBINSTALLATION_TYPES);
        }

        boolean hasInvalidFuelAndElectricityExchangeabilityInput = subInstallations.stream()
                .anyMatch(subInstallation ->
                        (subInstallation.getSubInstallationType().isFuelElectricityExchangeable() && ObjectUtils.isEmpty(subInstallation.getFuelAndElectricityExchangeability())) ||
                                (!subInstallation.getSubInstallationType().isFuelElectricityExchangeable() && !ObjectUtils.isEmpty(subInstallation.getFuelAndElectricityExchangeability()))
                );
        if (hasInvalidFuelAndElectricityExchangeabilityInput) {
            violationErrors.add(ERROR_FUEL_ELECTRICITY_EXCHANGEABILITY);
        }

        boolean hasInvalidDirectlyAttributableEmissionsInput = subInstallations.stream()
                .anyMatch(subInstallation ->
                        !validateDirectlyAttributableEmissions(subInstallation.getSubInstallationType(), subInstallation.getDirectlyAttributableEmissions())
                );
        if (hasInvalidDirectlyAttributableEmissionsInput) {
            violationErrors.add(ERROR_DIRECTLY_ATTRIBUTABLE_EMISSIONS);
        }

       boolean hasInvalidImportedExportedMeasurableHeat = subInstallations.stream()
                .anyMatch(subInstallation ->
                        !validateImportedExportedMeasurableHeat(subInstallation)
                );

        if (hasInvalidImportedExportedMeasurableHeat) {
            violationErrors.add(ERR0R_IMPORTED_EXPORTED_MEASURABLE_HEAT_DATA_SOURCE);
        }

        boolean hasInvalidWasteGasBalanceInput = subInstallations.stream()
                .filter(subInstallation -> subInstallation.getWasteGasBalance() != null)
                .filter(subInstallation -> !subInstallation.getWasteGasBalance().getWasteGasActivities().isEmpty() ||
                        !subInstallation.getWasteGasBalance().getDataSources().isEmpty())
                .filter(subInstallation -> !subInstallation.getWasteGasBalance().getWasteGasActivities()
                        .contains(WasteGasActivity.NO_WASTE_GAS_ACTIVITIES))
                .anyMatch(subInstallation -> subInstallation.getWasteGasBalance().getDataSources().stream()
                        .anyMatch(wasteGasBalanceEnergyFlowDataSource ->
                                !subInstallation.getWasteGasBalance().getWasteGasActivities().containsAll(wasteGasBalanceEnergyFlowDataSource.getWasteGasActivityDetails().keySet()) ||
                                        wasteGasBalanceEnergyFlowDataSource.getWasteGasActivityDetails().values().stream()
                                                .allMatch(this::isWasteGasBalanceEnergyFlowDataSourceDetailsEmpty)
                        )
                );
        if (hasInvalidWasteGasBalanceInput) {
            violationErrors.add(ERROR_INVALID_WASTE_GAS_INPUT);
        }

        boolean hasInvalidSpecialProductInput = subInstallations.stream()
                .anyMatch(subInstallation ->
                        !validateSpecialProduct(subInstallation.getSubInstallationType(), subInstallation.getSpecialProduct())
                );

        if (hasInvalidSpecialProductInput) {
            violationErrors.add(ERROR_INVALID_SPECIAL_PRODUCT_INPUT);
        }

        boolean hasInvalidAnnualLevelInput = subInstallations.stream().anyMatch(subInstallation ->
                !validateAnnualLevel(subInstallation));

        if(hasInvalidAnnualLevelInput) {
            violationErrors.add(ERROR_INVALID_ANNUAL_LEVEL_INPUT);
        }

        boolean hasInvalidFuelInputAndRelevantEmissionFactor = subInstallations.stream().anyMatch(subInstallation ->
                !validateFuelInputAndRelevantEmissionsFactor(subInstallation));

        if(hasInvalidFuelInputAndRelevantEmissionFactor) {
            violationErrors.add(ERR0R_FUEL_INPUT_DATA_SOURCE);
        }

        boolean hasInvalidMeasurableHeatInput = subInstallations.stream().anyMatch(subInstallation ->
                !validateMeasurableHeat(subInstallation));

        if(hasInvalidMeasurableHeatInput) {
            violationErrors.add(ERR0R_INVALID_MEASURABLE_HEAT);
        }

        validateMethodTask(subInstallations, digitizedPlan.getMethodTask(), violationErrors);

        EnergyFlows energyFlows = digitizedPlan.getEnergyFlows();
        validateEnergyFlows(energyFlows, violationErrors);

        if (!ObjectUtils.isEmpty(violationErrors)) {
            return PermitValidationResult.invalidPermit(
                    List.of(new PermitViolation(PermitViolation.PermitViolationMessage.INVALID_DIGITIZED_MMP_SUB_INSTALLATION, String.join(", ", violationErrors)))
            );
        }

        return PermitValidationResult.validPermit();
    }

    private boolean validateSpecialProduct(SubInstallationType subInstallationType, SpecialProduct specialProduct){
        // If the SubInstallationType does not have a special product, validation passes by default
        if (!subInstallationType.isHasSpecialProduct()) {
            return true;
        }

        // Handle specific validation based on the specialProductType
        return switch (specialProduct.getSpecialProductType()) {
            case REFINERY_PRODUCTS -> {
                // Check if specialProduct is of type RefineryProductsSP before casting
                if (!(specialProduct instanceof RefineryProductsSP refineryProduct)) {
                    throw new IllegalArgumentException("Special product is not of type RefineryProductsSP");
                }
                yield dataSourceValidatorFactory.getValidator(SpecialProduct.class).validateDataSources(refineryProduct);
            }
            case AROMATICS -> {
                // Check if specialProduct is of type AromaticsSP before casting
                if (!(specialProduct instanceof AromaticsSP aromatics)) {
                    throw new IllegalArgumentException("Special product is not of type AromaticsSP");
                }
                yield dataSourceValidatorFactory.getValidator(SpecialProduct.class).validateDataSources(aromatics);

            }
            case ETHYLENE_OXIDE_ETHYLENE_GLYCOLS -> {
                // Check if specialProduct is of type EthyleneOxideEthyleneGlycolsSP before casting
                if (!(specialProduct instanceof EthyleneOxideEthyleneGlycolsSP ethyleneGlycols)) {
                    throw new IllegalArgumentException("Special product is not of type EthyleneOxideEthyleneGlycolsSP");
                }

                yield ethyleneGlycols.getDataSources().stream()
                        .allMatch(dataSource -> {
                            EthyleneOxideEthyleneGlycolsDetails details = dataSource.getDetails();

                            // Validate that at least one field in details is not empty
                            return !isEthyleneOxideEthyleneGlycolsDetailsEmpty(details);
                        });
            }
            case HYDROGEN -> {
                if (!(specialProduct instanceof HydrogenSP hydrogen)) {
                    throw new IllegalArgumentException("Special product is not of type HydrogenSP");
                }
                yield dataSourceValidatorFactory.getValidator(SpecialProduct.class).validateDataSources(hydrogen);
            }
            case SYNTHESIS_GAS -> {
                if (!(specialProduct instanceof SynthesisGasSP synthesisGas)) {
                    throw new IllegalArgumentException("Special product is not of type synthesisGasSP");
                }
                yield dataSourceValidatorFactory.getValidator(SpecialProduct.class).validateDataSources(synthesisGas);
            }
                // Additional cases can be added here for other specialProductTypes as needed
                default -> true; // Return true for any unhandled specialProductType
            };
        }

        private boolean validateDirectlyAttributableEmissions(SubInstallationType subInstallationType, DirectlyAttributableEmissions directlyAttributableEmissions){
            if (ObjectUtils.isEmpty(directlyAttributableEmissions))
                return DirectlyAttributableEmissions.getEmptyDAE_SubInstallationTypes().contains(subInstallationType);

            return switch (directlyAttributableEmissions.getDirectlyAttributableEmissionsType()) {
                case PRODUCT_BENCHMARK -> {
                    // Check if DirectlyAttributableEmissions is of type DirectlyAttributableEmissionsPB before casting
                    if (!(directlyAttributableEmissions instanceof DirectlyAttributableEmissionsPB directlyAttributableEmissionsPB)) {
                        throw new IllegalArgumentException("DirectlyAttributableEmissions is not of type DirectlyAttributableEmissionsPB");
                    }
                    yield subInstallationType.getCategory().equals(SubInstallationCategory.PRODUCT_BENCHMARK) &&
                            dataSourceValidatorFactory.getValidator(DirectlyAttributableEmissionsPB.class).validateDataSources(directlyAttributableEmissionsPB);
                }
                case FALLBACK_APPROACH -> {
                    // Check if DirectlyAttributableEmissions is of type DirectlyAttributableEmissionsFA before casting
                    if (!(directlyAttributableEmissions instanceof DirectlyAttributableEmissionsFA directlyAttributableEmissionsFA)) {
                        throw new IllegalArgumentException("DirectlyAttributableEmissions is not of type DirectlyAttributableEmissionsFA");
                    }
                    if ((SubInstallationType.FUEL_BENCHMARK_CL.equals(subInstallationType) || SubInstallationType.FUEL_BENCHMARK_NON_CL.equals(subInstallationType))
                            && directlyAttributableEmissions.getAttribution() == null) {
                        throw new IllegalArgumentException("Attribution with subInstallationType FUEL_BENCHMARK_CL or FUEL_BENCHMARK_NON_CL, must not be null");
                    }
                    yield DirectlyAttributableEmissionsFA.getSupportedSubInstallationTypes().contains(subInstallationType);
                }
            };
        }

        private boolean isImportedExportedMeasurableHeatDataSourceEmpty(ImportedExportedMeasurableHeatEnergyFlowDataSource dataSource) {
        return ObjectUtils.isEmpty(dataSource.getMeasurableHeatImported()) &&
                ObjectUtils.isEmpty(dataSource.getMeasurableHeatPulp()) &&
                ObjectUtils.isEmpty(dataSource.getMeasurableHeatNitricAcid()) &&
                ObjectUtils.isEmpty(dataSource.getMeasurableHeatExported()) &&
                ObjectUtils.isEmpty(dataSource.getNetMeasurableHeatFlows());
        }

        private boolean isWasteGasBalanceEnergyFlowDataSourceDetailsEmpty(WasteGasBalanceEnergyFlowDataSourceDetails details) {
        return ObjectUtils.isEmpty(details.getEntity()) &&
                ObjectUtils.isEmpty(details.getEnergyContent()) &&
                ObjectUtils.isEmpty(details.getEmissionFactor());
        }

        private boolean isEthyleneOxideEthyleneGlycolsDetailsEmpty(EthyleneOxideEthyleneGlycolsDetails ethyleneGlycolsDetails) {
        return ObjectUtils.isEmpty(ethyleneGlycolsDetails.getEthyleneOxide()) &&
                ObjectUtils.isEmpty(ethyleneGlycolsDetails.getMonothyleneGlycol()) &&
                ObjectUtils.isEmpty(ethyleneGlycolsDetails.getDiethyleneGlycol()) &&
                ObjectUtils.isEmpty(ethyleneGlycolsDetails.getTriethyleneGlycol());
        }

    private boolean validateAnnualLevel (SubInstallation subInstallation) {
       AnnualLevel annualLevel = subInstallation.getAnnualLevel();
       return switch (annualLevel.getAnnualLevelType()) {
            case PRODUCTION -> {
                if (!(annualLevel instanceof AnnualProductionLevel)) {
                    throw new IllegalArgumentException("Annual level's type should be AnnualProductionLevel");
                }
                yield subInstallation.getSubInstallationType().getCategory().equals(SubInstallationCategory.PRODUCT_BENCHMARK);
            }
            case ACTIVITY_HEAT -> {
                if (!(annualLevel instanceof AnnualActivityHeatLevel annualActivityHeatLevel)) {
                    throw new IllegalArgumentException("Annual level's type should be AnnualActivityHeatLevel");
                }
                yield annualActivityHeatLevel.getSupportedSubInstallationTypes().contains(subInstallation.getSubInstallationType()) &&
                        dataSourceValidatorFactory.getValidator(AnnualActivityLevel.class).validateDataSources(annualActivityHeatLevel);
            }
            case ACTIVITY_FUEL -> {
                if (!(annualLevel instanceof AnnualActivityFuelLevel annualActivityFuelLevel)) {
                    throw new IllegalArgumentException("Annual level's type should be AnnualActivityFuelLevel");
                }
                yield annualActivityFuelLevel.getSupportedSubInstallationTypes().contains(subInstallation.getSubInstallationType()) &&
                        dataSourceValidatorFactory.getValidator(AnnualActivityLevel.class).validateDataSources(annualActivityFuelLevel);
            }
           case ACTIVITY_PROCESS -> {
               if (!(annualLevel instanceof AnnualActivityProcessLevel annualActivityProcessLevel)) {
                   throw new IllegalArgumentException("Annual level's type should be AnnualActivityProcessLevel");
               }
               yield annualActivityProcessLevel.getSupportedSubInstallationTypes().contains(subInstallation.getSubInstallationType());
           }
       };
    }

    private boolean validateMeasurableHeat(SubInstallation subInstallation) {
        MeasurableHeat measurableHeat = subInstallation.getMeasurableHeat();

        if (ObjectUtils.isEmpty(measurableHeat)) {
            return SubInstallationCategory.PRODUCT_BENCHMARK.equals(subInstallation.getSubInstallationType().getCategory()) ||
                    subInstallation.getSubInstallationType().equals(SubInstallationType.PROCESS_EMISSIONS_CL) ||
                    subInstallation.getSubInstallationType().equals(SubInstallationType.PROCESS_EMISSIONS_NON_CL);
        }

        SubInstallationType subInstallationType = subInstallation.getSubInstallationType();

        boolean validDataSources = Stream.of(
                        Optional.ofNullable(measurableHeat.getMeasurableHeatImported())
                                .map(imported -> dataSourceValidatorFactory.getValidator(MeasurableHeatImported.class)
                                        .validateDataSources(imported)),
                        Optional.ofNullable(measurableHeat.getMeasurableHeatExported())
                                .map(exported -> dataSourceValidatorFactory.getValidator(MeasurableHeatExported.class)
                                        .validateDataSources(exported))
                ).flatMap(Optional::stream)
                .allMatch(Boolean::booleanValue);

        boolean isMeasurableHeatProducedValid =
                (ObjectUtils.isEmpty(measurableHeat.getMeasurableHeatProduced()) &&
                        !MeasurableHeat.getMeasurableHeatProducedSupportingSubInstallationTypes().contains(subInstallationType)) ||
                        (!ObjectUtils.isEmpty(measurableHeat.getMeasurableHeatProduced()) &&
                                MeasurableHeat.getMeasurableHeatProducedSupportingSubInstallationTypes().contains(subInstallationType));

        boolean isMeasurableHeatImportedValid =
                (ObjectUtils.isEmpty(measurableHeat.getMeasurableHeatImported()) &&
                        !MeasurableHeat.getMeasurableHeatImportedSupportingSubInstallationTypes().contains(subInstallationType)) ||
                        (!ObjectUtils.isEmpty(measurableHeat.getMeasurableHeatImported()) &&
                                MeasurableHeat.getMeasurableHeatImportedSupportingSubInstallationTypes().contains(subInstallationType));

        boolean isMeasurableHeatExportedValid =
                (ObjectUtils.isEmpty(measurableHeat.getMeasurableHeatExported()) &&
                        !MeasurableHeat.getMeasurableHeatExportedSupportingSubInstallationTypes().contains(subInstallationType)) ||
                        (!ObjectUtils.isEmpty(measurableHeat.getMeasurableHeatExported()) &&
                                MeasurableHeat.getMeasurableHeatExportedSupportingSubInstallationTypes().contains(subInstallationType));


        return validDataSources && isMeasurableHeatProducedValid && isMeasurableHeatImportedValid && isMeasurableHeatExportedValid;
    }


    private boolean validateFuelInputAndRelevantEmissionsFactor(SubInstallation subInstallation) {
        FuelInputAndRelevantEmissionFactor fuelInputAndRelevantEmissionFactor = subInstallation.getFuelInputAndRelevantEmissionFactor();
        if(ObjectUtils.isEmpty(fuelInputAndRelevantEmissionFactor)) {
            return FuelInputAndRelevantEmissionFactor.getEmptyFIandREF_SubInstallationTypes().contains(subInstallation.getSubInstallationType());
        }
        switch (fuelInputAndRelevantEmissionFactor.getFuelInputAndRelevantEmissionFactorType()) {
            case PRODUCT_BENCHMARK -> {
                if(!(fuelInputAndRelevantEmissionFactor instanceof FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactorPB)) {
                    throw new IllegalArgumentException("FuelInputAndRelevantEmissionFactor is not of type FuelInputAndRelevantEmissionFactorPB");
                }
                return dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorPB.class)
                        .validateDataSources(fuelInputAndRelevantEmissionFactorPB);

            }
            case FALLBACK_APPROACH -> {
                if(!(fuelInputAndRelevantEmissionFactor instanceof FuelInputAndRelevantEmissionFactorFA fuelInputAndRelevantEmissionFactorFA)) {
                    throw new IllegalArgumentException("FuelInputAndRelevantEmissionFactor is not of type FuelInputAndRelevantEmissionFactorFA");
                }
                return dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorFA.class)
                        .validateDataSources(fuelInputAndRelevantEmissionFactorFA);
            }
            case HEAT_FALLBACK_APPROACH -> {
                if(!(fuelInputAndRelevantEmissionFactor instanceof FuelInputAndRelevantEmissionFactorHeatFA fuelInputAndRelevantEmissionFactorHeatFA)) {
                    throw new IllegalArgumentException("FuelInputAndRelevantEmissionFactor is not of type FuelInputAndRelevantEmissionFactorHeatFA");
                }
                if (!fuelInputAndRelevantEmissionFactorHeatFA.isExists()) return true;
                return dataSourceValidatorFactory.getValidator(FuelInputAndRelevantEmissionFactorFA.class)
                        .validateDataSources(fuelInputAndRelevantEmissionFactorHeatFA);
            }
            default -> throw new IllegalArgumentException("Unrecognized FuelInputAndRelevantEmissionFactorType for validation");
        }
    }

    private void validateEnergyFlows(EnergyFlows energyFlows, List<String> violationErrors) {
        if (energyFlows != null && energyFlows.getFuelInputFlows() != null) {
            boolean hasInvalidFuelInputDataSources = !validateFuelInputDataSources(energyFlows.getFuelInputFlows().getFuelInputDataSources());
            if (hasInvalidFuelInputDataSources) {
                violationErrors.add(ERROR_INVALID_FUEL_INPUT_FLOWS_DATASOURCES);
            }
        }
        if (energyFlows != null && energyFlows.getMeasurableHeatFlows() != null && energyFlows.getMeasurableHeatFlows().getMeasurableHeatFlowsRelevant()) {
            boolean hasInvalidMeasurableHeatFlowsDataSources = !validateMeasurableHeatFlowsDataSources(energyFlows.getMeasurableHeatFlows().getMeasurableHeatFlowsDataSources());
            if (hasInvalidMeasurableHeatFlowsDataSources) {
                violationErrors.add(ERROR_INVALID_MEASURABLE_HEAT_FLOWS_DATASOURCES);
            }
        }
        if (energyFlows != null && energyFlows.getWasteGasFlows() != null && energyFlows.getWasteGasFlows().getWasteGasFlowsRelevant()) {
            boolean hasInvalidWasteGasFlowsDataSources = !validateWasteGasFlowsDataSources(energyFlows.getWasteGasFlows().getWasteGasFlowsDataSources());
            if (hasInvalidWasteGasFlowsDataSources) {
                violationErrors.add(ERROR_INVALID_WASTE_GAS_FLOWS_DATASOURCES);
            }
        }
    }

    private boolean validateFuelInputDataSources(List<FuelInputDataSource> fuelInputDataSources) {
        if (fuelInputDataSources == null || fuelInputDataSources.isEmpty()) {
            return false;
        }
        for (int i = 0; i < fuelInputDataSources.size(); i++) {
            FuelInputDataSource dt = fuelInputDataSources.get(i);
            if (dt == null) {
                return false;
            }
            if (i == 0) {
                // First entry: all fields must be non-null
                if (dt.getFuelInput() == null || dt.getEnergyContent() == null) {
                    return false;
                }
            } else {
                // Entries 1-5: at least one of fuelInput or energyContent must be non-null
                if (dt.getFuelInput() == null && dt.getEnergyContent() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateMeasurableHeatFlowsDataSources(List<MeasurableHeatFlowsDataSource> measurableHeatFlowsDataSources) {
        if (measurableHeatFlowsDataSources == null || measurableHeatFlowsDataSources.isEmpty()) {
            return false;
        }
        for (int i = 0; i < measurableHeatFlowsDataSources.size(); i++) {
            MeasurableHeatFlowsDataSource dt = measurableHeatFlowsDataSources.get(i);
            if (dt == null) {
                return false;
            }
            if (i == 0) {
                // First entry: all fields must be non-null
                if (dt.getQuantification() == null || dt.getNet() == null) {
                    return false;
                }
            } else {
                // Entries 1-5: at least one of quantification or net must be non-null
                if (dt.getQuantification() == null && dt.getNet() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateWasteGasFlowsDataSources(List<WasteGasFlowsDataSource> wasteGasFlowsDataSources) {
        if (wasteGasFlowsDataSources == null || wasteGasFlowsDataSources.isEmpty()) {
            return false;
        }
        for (int i = 0; i < wasteGasFlowsDataSources.size(); i++) {
            WasteGasFlowsDataSource dt = wasteGasFlowsDataSources.get(i);
            if (dt == null) {
                return false;
            }
            if (i == 0) {
                // First entry: all fields must be non-null
                if (dt.getQuantification() == null || dt.getEnergyContent() == null) {
                    return false;
                }
            } else {
                // Entries 1-5: at least one of quantification or energy content must be non-null
                if (dt.getQuantification() == null && dt.getEnergyContent() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private void validateMethodTask(List<SubInstallation> subInstallations, MethodTask methodTask, List<String> violationErrors) {
        if(methodTask.getPhysicalPartsAndUnitsAnswer()==null) {
            if(subInstallations.size()>1){
                violationErrors.add("If there is more than one sub-installation then the physical parts and " +
                        "units answer cannot be null");
            }
            return;
        }
        if(subInstallations.size()<2 && methodTask.getPhysicalPartsAndUnitsAnswer()) {
            violationErrors.add("If there is only one subInstallation then the physical parts and units answer cannot exist");
            return;
        }

        methodTask.getConnections().forEach(methodTaskConnection -> {
            methodTaskConnection.getSubInstallations().stream()
                    .filter(methodTaskSubInstallationType ->
                            subInstallations.stream()
                                    .noneMatch(subInstallation ->
                                            subInstallation.getSubInstallationType().equals(methodTaskSubInstallationType)))
                    .forEach(missingType ->
                            violationErrors.add(String.format("Missing %s from sub installations of digital plan", missingType)));
        });
    }

    private boolean validateImportedExportedMeasurableHeat(SubInstallation subInstallation) {
        ImportedExportedMeasurableHeat importedExportedMeasurableHeat = subInstallation.getImportedExportedMeasurableHeat();
        if(ObjectUtils.isEmpty(importedExportedMeasurableHeat)) {
            return subInstallation.getSubInstallationType().getCategory().equals(SubInstallationCategory.FALLBACK_APPROACH);
        }
        if(importedExportedMeasurableHeat.getFuelBurnCalculationTypes().contains(ImportedExportedMeasurableHeatType.NO_MEASURABLE_HEAT)) {
            return true;
        }

        return dataSourceValidatorFactory.getValidator(ImportedExportedMeasurableHeat.class).validateDataSources(importedExportedMeasurableHeat);
    }

    public boolean validateSubInstallationTypeAndSize(List<SubInstallation> subInstallations) {
        long productBenchmarkCount = subInstallations.stream().filter(subInstallation ->
                subInstallation.getSubInstallationType().getCategory().equals(SubInstallationCategory.PRODUCT_BENCHMARK)).count();
        long fallbackApproachCount = subInstallations.stream().filter(subInstallation ->
                subInstallation.getSubInstallationType().getCategory().equals(SubInstallationCategory.FALLBACK_APPROACH)).count();

        return productBenchmarkCount<=10 && fallbackApproachCount<=7;
    }

    private void validateOpredInstallation(List<SubInstallation> subInstallations, List<String> violationErrors) {
        if(subInstallations.stream().anyMatch(subInstallation ->
                subInstallation.getSubInstallationType().getCategory().equals(SubInstallationCategory.PRODUCT_BENCHMARK))) {
            violationErrors.add(ERROR_OPRED_SUBINSTALLATION);
        }
    }
}

