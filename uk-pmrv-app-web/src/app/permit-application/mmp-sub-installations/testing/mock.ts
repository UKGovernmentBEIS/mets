import { DigitizedPlan, SubInstallation } from 'pmrv-api';

export const mockMethodTask: DigitizedPlan['methodTask'] = {
  physicalPartsAndUnitsAnswer: true,
  connections: [
    {
      itemId: '0',
      itemName: 'Test 1',
      subInstallations: ['ADIPIC_ACID', 'AMMONIA'],
    },
  ],
  assignParts: 'Test assign parts',
  avoidDoubleCount: 'Test avoid double count',
  avoidDoubleCountToggle: true,
};

export const mockDigitizedPlanDetails: DigitizedPlan = {
  subInstallations: [
    {
      subInstallationNo: '0',
      subInstallationType: 'AMMONIA',
      description: 'description',
      supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c114'],
    } as SubInstallation,
  ],
  methodTask: mockMethodTask,
};

export const mockDigitizedPlanAnnualProductionLevel = {
  subInstallations: [
    {
      ...mockDigitizedPlanDetails.subInstallations[0],
      annualLevel: {
        annualLevelType: 'PRODUCTION',
        quantityProductDataSources: [{ quantityProduct: 'METHOD_MONITORING_PLAN', quantityProductDataSourceNo: '0' }],
        annualQuantityDeterminationMethod: 'CONTINUAL_METERING_PROCESS',
        methodologyAppliedDescription: 'description',
        hierarchicalOrder: { followed: true },
        trackingMethodologyDescription: 'description',
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c115'],
      },
    },
  ],
};

export const mockDigitizedPlanExchangeability = {
  subInstallations: [
    {
      ...mockDigitizedPlanAnnualProductionLevel.subInstallations[0],
      fuelAndElectricityExchangeability: {
        fuelAndElectricityExchangeabilityEnergyFlowDataSources: [
          {
            relevantElectricityConsumption: 'LEGAL_METROLOGICAL_CONTROL_READING',
            energyFlowDataSourceNo: '0',
          },
        ],
        methodologyAppliedDescription: 'description',
        hierarchicalOrder: { followed: true },
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c116'],
      },
    },
  ],
};

export const mockDigitizedPlanImportedMeasureableHeat = {
  subInstallations: [
    {
      ...mockDigitizedPlanExchangeability.subInstallations[0],
      importedMeasurableHeatFlow: {
        exist: false,
      },
    },
  ],
};

export const mockDigitizedPlanDirectlyAttributableEmissions = {
  subInstallations: [
    {
      ...mockDigitizedPlanImportedMeasureableHeat.subInstallations[0],
      directlyAttributableEmissions: {
        attribution: 'attribution',
        furtherInternalSourceStreamsRelevant: true,
        dataSources: [
          {
            amounts: 'METHOD_MONITORING_PLAN',
            importedExportedAmountsDataSourceNo: '0',
            biomassContent: null,
            emissionFactorOrCarbonContent: null,
            energyContent: null,
          },
        ],
        directlyAttributableEmissionsType: 'PRODUCT_BENCHMARK',
        methodologyAppliedDescription: 'description',
        transferredCO2ImportedOrExportedRelevant: true,
        amountsMonitoringDescription: 'description',
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c117'],
      },
    },
  ],
};

export const mockDigitizedPlanFuelInputRelevantEmissionFactor = {
  subInstallations: [
    {
      ...mockDigitizedPlanDirectlyAttributableEmissions.subInstallations[0],
      fuelInputAndRelevantEmissionFactor: {
        exist: true,
        dataSources: [
          {
            fuelInput: 'METHOD_MONITORING_PLAN',
            fuelInputDataSourceNo: '0',
            weightedEmissionFactor: 'CALCULATION_METHOD_MONITORING_PLAN',
          },
        ],
        methodologyAppliedDescription: 'description',
        hierarchicalOrder: { followed: true },
        fuelInputAndRelevantEmissionFactorType: 'PRODUCT_BENCHMARK',
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c118'],
      },
    },
  ],
};

export const mockDigitizedPlanImportedExportedMeasurableHeat = {
  subInstallations: [
    {
      ...mockDigitizedPlanFuelInputRelevantEmissionFactor.subInstallations[0],
      importedExportedMeasurableHeat: {
        fuelBurnCalculationTypes: ['MEASURABLE_HEAT_IMPORTED'],
        dataSources: [
          {
            netMeasurableHeatFlows: 'MEASUREMENTS',
            energyFlowDataSourceNo: '0',
            measurableHeatExported: undefined,
            measurableHeatImported: null,
            measurableHeatNitricAcid: undefined,
            measurableHeatPulp: undefined,
          },
        ],
        dataSourcesMethodologyAppliedDescription: 'description',
        methodologyDeterminationDescription: 'description',
        hierarchicalOrder: { followed: true },
        measurableHeatImportedFromPulp: false,
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c119'],
      },
    },
  ],
};

export const mockDigitizedPlanWasteGasBalance = {
  subInstallations: [
    {
      ...mockDigitizedPlanImportedExportedMeasurableHeat.subInstallations[0],
      wasteGasBalance: {
        dataSources: [
          {
            energyFlowDataSourceNo: '0',
            wasteGasActivityDetails: {
              WASTE_GAS_PRODUCED: {
                entity: 'METHOD_MONITORING_PLAN',
                emissionFactor: undefined,
                energyContent: undefined,
              },
            },
          },
        ],
        wasteGasActivities: ['WASTE_GAS_PRODUCED'],
        dataSourcesMethodologyAppliedDescription: 'description',
        hierarchicalOrder: { followed: true },
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c120'],
      },
    },
  ],
};

export const mockDigitizedPlanSPrefineryProducts = {
  subInstallations: [
    {
      ...mockDigitizedPlanWasteGasBalance.subInstallations[0],
      specialProduct: {
        refineryProductsRelevantCWTFunctions: ['ATMOSPHERIC_CRUDE_DISTILLATION'],
        methodologyAppliedDescription: 'description',
        hierarchicalOrder: {
          followed: true,
        },
        refineryProductsDataSources: [
          {
            dataSourceNo: '0',
            details: {
              ATMOSPHERIC_CRUDE_DISTILLATION: 'METHOD_MONITORING_PLAN',
            },
          },
        ],
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c121'],
        specialProductType: 'REFINERY_PRODUCTS',
      },
    },
  ],
};

export const mockDigitizedPlanSPAromatics = {
  subInstallations: [
    {
      ...mockDigitizedPlanWasteGasBalance.subInstallations[0],
      specialProduct: {
        relevantCWTFunctions: ['NAPHTHA_GASOLINE_HYDROTREATER'],
        methodologyAppliedDescription: 'description',
        hierarchicalOrder: {
          followed: true,
        },
        dataSources: [
          {
            dataSourceNo: '0',
            details: {
              NAPHTHA_GASOLINE_HYDROTREATER: 'METHOD_MONITORING_PLAN',
            },
          },
        ],
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c121'],
        specialProductType: 'AROMATICS',
      },
    },
  ],
};

export const mockDigitizedPlanSPLime = {
  subInstallations: [
    {
      ...mockDigitizedPlanWasteGasBalance.subInstallations[0],
      specialProduct: {
        methodologyAppliedDescription: 'description',
        hierarchicalOrder: {
          followed: true,
        },
        dataSources: [
          {
            dataSourceNo: '0',
            detail: 'CALCULATION_METHOD_MONITORING_PLAN',
          },
        ],
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c121'],
        specialProductType: 'LIME',
      },
    },
  ],
};

export const mockDigitizedPlanSPDolime = {
  subInstallations: [
    {
      ...mockDigitizedPlanSPLime.subInstallations[0],
      specialProduct: {
        ...mockDigitizedPlanSPLime.subInstallations[0].specialProduct,
        specialProductType: 'DOLIME',
      },
    },
  ],
};

export const mockDigitizedPlanSPSteamCracking = {
  subInstallations: [
    {
      ...mockDigitizedPlanSPLime.subInstallations[0],
      specialProduct: {
        ...mockDigitizedPlanSPLime.subInstallations[0].specialProduct,
        dataSources: [
          {
            dataSourceNo: '0',
            detail: 'METHOD_MONITORING_PLAN',
          },
        ],
        specialProductType: 'STEAM_CRACKING',
      },
    },
  ],
};

export const mockDigitizedPlanSPHydrogen = {
  subInstallations: [
    {
      ...mockDigitizedPlanSPSteamCracking.subInstallations[0],
      specialProduct: {
        ...mockDigitizedPlanSPSteamCracking.subInstallations[0].specialProduct,
        dataSources: [
          {
            details: {
              HYDROGEN_TOTAL_PRODUCTION: 'METHOD_MONITORING_PLAN',
              HYDROGEN_VOLUME_FRACTION: 'LABORATORY_ANALYSES_SECTION_61',
            },
            dataSourceNo: '0',
          },
        ],
        specialProductType: 'HYDROGEN',
      },
    },
  ],
};

export const mockDigitizedPlanSPSynthesisGas = {
  subInstallations: [
    {
      ...mockDigitizedPlanSPSteamCracking.subInstallations[0],
      specialProduct: {
        ...mockDigitizedPlanSPSteamCracking.subInstallations[0].specialProduct,
        dataSources: [
          {
            details: {
              SYNTHESIS_GAS_TOTAL_PRODUCTION: 'METHOD_MONITORING_PLAN',
              SYNTHESIS_GAS_COMPOSITION_DATA: 'LABORATORY_ANALYSES_SECTION_61',
            },
            dataSourceNo: '0',
          },
        ],
        specialProductType: 'SYNTHESIS_GAS',
      },
    },
  ],
};

export const mockDigitizedPlanSPEthyleneOxideGlycols = {
  subInstallations: [
    {
      ...mockDigitizedPlanSPSteamCracking.subInstallations[0],
      specialProduct: {
        ...mockDigitizedPlanSPSteamCracking.subInstallations[0].specialProduct,
        dataSources: [
          {
            details: {
              ETHYLEN_OXIDE: 'METHOD_MONITORING_PLAN',
              MONOTHYLENE_GLYCOL: 'METHOD_MONITORING_PLAN',
              DIETHYLENE_GLYCOL: 'METHOD_MONITORING_PLAN',
              TRIETHYLENE_GLYCOL: 'METHOD_MONITORING_PLAN',
            },
            dataSourceNo: '0',
          },
        ],
        specialProductType: 'ETHYLENE_OXIDE_ETHYLENE_GLYCOLS',
      },
    },
  ],
};

export const mockDigitizedPlanSPVinylChlorideMonomer = {
  subInstallations: [
    {
      ...mockDigitizedPlanSPSteamCracking.subInstallations[0],
      specialProduct: {
        ...mockDigitizedPlanSPSteamCracking.subInstallations[0].specialProduct,
        dataSources: [
          {
            dataSourceNo: '0',
            detail: 'NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A',
          },
        ],
        specialProductType: 'VINYL_CHLORIDE_MONOMER',
      },
    },
  ],
};

export const mockDigitizedPlanFallbackDetails = {
  subInstallations: [
    {
      subInstallationNo: '0',
      subInstallationType: 'HEAT_BENCHMARK_CL',
      description: 'description',
      supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c115'],
    },
  ],
};

export const mockDigitizedPlanFallbackDetailsFuel = {
  subInstallations: [
    {
      subInstallationNo: '0',
      subInstallationType: 'FUEL_BENCHMARK_CL',
      description: 'description',
      supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c115'],
    },
  ],
};

export const mockDigitizedPlanFallbackDetailsProcess = {
  subInstallations: [
    {
      subInstallationNo: '0',
      subInstallationType: 'PROCESS_EMISSIONS_CL',
      description: 'description',
      supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c115'],
    },
  ],
};

export const mockDigitizedPlanAnnualHeatLevel = {
  subInstallations: [
    {
      ...mockDigitizedPlanFallbackDetails.subInstallations[0],
      annualLevel: {
        annualLevelType: 'ACTIVITY_HEAT',
        measurableHeatFlowList: [
          {
            quantification: 'LEGAL_METROLOGICAL_CONTROL_READING',
            net: 'MEASUREMENTS',
            measurableHeatFlowQuantificationNo: '0',
          },
        ],
        methodologyAppliedDescription: 'description',
        hierarchicalOrder: { followed: true },
        trackingMethodologyDescription: 'description',
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c116'],
      },
    },
  ],
};

export const mockDigitizedPlanAnnualFuelLevel = {
  subInstallations: [
    {
      ...mockDigitizedPlanFallbackDetails.subInstallations[0],
      subInstallationType: 'FUEL_BENCHMARK_CL',
      annualLevel: {
        annualLevelType: 'ACTIVITY_FUEL',
        fuelDataSources: [
          {
            fuelInput: 'METHOD_MONITORING_PLAN',
            energyContent: 'CALCULATION_METHOD_MONITORING_PLAN',
            dataSourceQuantificationNumber: '0',
          },
        ],
        methodologyAppliedDescription: 'description',
        hierarchicalOrder: { followed: true },
        trackingMethodologyDescription: 'description',
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c116'],
      },
    },
  ],
};

export const mockDigitizedPlanAnnualProcessLevel = {
  subInstallations: [
    {
      ...mockDigitizedPlanFallbackDetails.subInstallations[0],
      subInstallationType: 'PROCESS_EMISSIONS_CL',
      annualLevel: {
        annualLevelType: 'ACTIVITY_PROCESS',
        methodologyAppliedDescription: 'description',
        trackingMethodologyDescription: 'description',
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c116'],
      },
    },
  ],
};

export const mockDigitizedPlanDirectlyAttributableEmissionsFA = {
  subInstallations: [
    {
      ...mockDigitizedPlanAnnualHeatLevel.subInstallations[0],
      directlyAttributableEmissions: {
        attribution: 'attribution',
        directlyAttributableEmissionsType: 'FALLBACK_APPROACH',
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c117'],
      },
    },
  ],
};

export const mockDigitizedPlanFuelInputRelevantEmissionFactorFA = {
  subInstallations: [
    {
      ...mockDigitizedPlanDirectlyAttributableEmissionsFA.subInstallations[0],
      fuelInputAndRelevantEmissionFactor: {
        exists: true,
        wasteGasesInput: false,
        dataSources: [
          {
            fuelInput: 'METHOD_MONITORING_PLAN',
            netCalorificValue: 'CALCULATION_METHOD_MONITORING_PLAN',
            weightedEmissionFactor: 'CALCULATION_METHOD_MONITORING_PLAN',
            fuelInputDataSourceNo: '0',
          },
        ],
        methodologyAppliedDescription: 'description',
        hierarchicalOrder: { followed: true },
        fuelInputAndRelevantEmissionFactorType: 'HEAT_FALLBACK_APPROACH',
        supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c118'],
      },
    },
  ],
};

export const mockDigitizedPlanFuelInputRelevantEmissionFactorFAYes = {
  subInstallations: [
    {
      ...mockDigitizedPlanFuelInputRelevantEmissionFactorFA.subInstallations[0],
      fuelInputAndRelevantEmissionFactor: {
        ...mockDigitizedPlanFuelInputRelevantEmissionFactorFA.subInstallations[0].fuelInputAndRelevantEmissionFactor,
        wasteGasesInput: true,
        dataSources: [
          {
            ...mockDigitizedPlanFuelInputRelevantEmissionFactorFA.subInstallations[0].fuelInputAndRelevantEmissionFactor
              .dataSources[0],
            wasteGasFuelInput: 'METHOD_MONITORING_PLAN',
            wasteGasNetCalorificValue: 'CALCULATION_METHOD_MONITORING_PLAN',
            emissionFactor: 'CALCULATION_METHOD_MONITORING_PLAN',
          },
        ],
      },
    },
  ],
};

export const mockDigitizedPlanMeasurableHeatProduced = {
  subInstallations: [
    {
      ...mockDigitizedPlanFuelInputRelevantEmissionFactorFAYes.subInstallations[0],
      measurableHeat: {
        measurableHeatProduced: {
          dataSources: [
            {
              heatProduced: 'LEGAL_METROLOGICAL_CONTROL_READING',
              dataSourceNo: '0',
            },
          ],
          methodologyAppliedDescription: 'description',
          hierarchicalOrder: { followed: true },
          produced: true,
          supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c116'],
        },
      },
    },
  ],
};

export const mockDigitizedPlanMeasurableHeatImported = {
  subInstallations: [
    {
      ...mockDigitizedPlanMeasurableHeatProduced.subInstallations[0],
      measurableHeat: {
        measurableHeatProduced: {
          ...mockDigitizedPlanMeasurableHeatProduced.subInstallations[0].measurableHeat.measurableHeatProduced,
        },
        measurableHeatImported: {
          dataSources: [
            {
              measurableHeatImportedActivityDetails: {
                MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES: {
                  entity: 'LEGAL_METROLOGICAL_CONTROL_READING',
                  netContent: 'MEASUREMENTS',
                },
              },
              dataSourceNo: '0',
            },
          ],
          measurableHeatImportedActivities: ['MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES'],
          methodologyAppliedDescription: 'description',
          methodologyDeterminationEmissionDescription: 'description',
          hierarchicalOrder: { followed: true },
          supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c120'],
        },
      },
    },
  ],
};

export const mockDigitizedPlanMeasurableHeatExported = {
  subInstallations: [
    {
      ...mockDigitizedPlanFuelInputRelevantEmissionFactorFA.subInstallations[0],
      measurableHeat: {
        measurableHeatExported: {
          dataSources: [
            {
              heatExported: 'LEGAL_METROLOGICAL_CONTROL_READING',
              netMeasurableHeatFlows: 'MEASUREMENTS',
              dataSourceNo: '0',
            },
          ],
          methodologyAppliedDescription: 'description',
          methodologyDeterminationEmissionDescription: 'description',
          hierarchicalOrder: { followed: true },
          measurableHeatExported: true,
          supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c118'],
        },
      },
    },
  ],
};
