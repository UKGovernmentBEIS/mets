import {
  CalculationOfCO2MonitoringApproach,
  CalculationOfPFCMonitoringApproach,
  CalculationSourceStreamCategoryAppliedTier,
  FallbackMonitoringApproach,
  FallbackSourceStreamCategoryAppliedTier,
  InherentCO2MonitoringApproach,
  MeasurementOfCO2EmissionPointCategoryAppliedTier,
  MeasurementOfCO2MonitoringApproach,
  MeasurementOfN2OEmissionPointCategoryAppliedTier,
  MeasurementOfN2OMonitoringApproach,
  PFCSourceStreamCategoryAppliedTier,
  TransferredCO2AndN2OMonitoringApproach,
} from 'pmrv-api';
// Source stream Category applied tier constants
export const calculationSourceStreamMapper: Partial<Record<keyof CalculationSourceStreamCategoryAppliedTier, string>> =
  {
    netCalorificValue: 'CALCULATION_CO2_Calorific',
    activityData: 'CALCULATION_CO2_Activity_Data',
    biomassFraction: 'CALCULATION_CO2_Biomass_Fraction',
    carbonContent: 'CALCULATION_CO2_Carbon_Content',
    conversionFactor: 'CALCULATION_CO2_Conversion_Factor',
    emissionFactor: 'CALCULATION_CO2_Emission_Factor',
    oxidationFactor: 'CALCULATION_CO2_Oxidation_Factor',
    sourceStreamCategory: 'CALCULATION_CO2_Category',
  };

const N2OSourceStreamMapper: Partial<Record<keyof MeasurementOfN2OEmissionPointCategoryAppliedTier, string>> = {
  appliedStandard: 'MEASUREMENT_N2O_Applied_Standard',
  measuredEmissions: 'MEASUREMENT_N2O_Measured_Emissions',
  emissionPointCategory: 'MEASUREMENT_N2O_Category',
};

const fallbackStreamMapper: Record<keyof FallbackSourceStreamCategoryAppliedTier, string> = {
  sourceStreamCategory: 'FALLBACK_Category',
};

const measurementStreamMapper: Partial<Record<keyof MeasurementOfCO2EmissionPointCategoryAppliedTier, string>> = {
  appliedStandard: 'MEASUREMENT_CO2_Applied_Standard',
  measuredEmissions: 'MEASUREMENT_CO2_Measured_Emissions',
  emissionPointCategory: 'MEASUREMENT_CO2_Category',
};

const PFCSourceStreamMapper: Record<keyof PFCSourceStreamCategoryAppliedTier, string> = {
  activityData: 'CALCULATION_PFC_Activity_Data',
  emissionFactor: 'CALCULATION_PFC_Emission_Factor',
  sourceStreamCategory: 'CALCULATION_PFC_Category',
};

// Monitor appproach constants
const calculationMonitoringApproachMapper: Record<
  keyof Pick<CalculationOfCO2MonitoringApproach, 'approachDescription' | 'samplingPlan'>,
  string
> = {
  approachDescription: 'CALCULATION_CO2_Description',
  samplingPlan: 'CALCULATION_CO2_Plan',
};

const N2OMonitoringApproachMapper: Record<
  keyof Omit<MeasurementOfN2OMonitoringApproach, 'emissionPointCategoryAppliedTiers' | 'type'>,
  string
> = {
  approachDescription: 'MEASUREMENT_N2O_Description',
  emissionDetermination: 'MEASUREMENT_N2O_Emission',
  gasFlowCalculation: 'MEASUREMENT_N2O_Gas',
  nitrousOxideConcentrationDetermination: 'MEASUREMENT_N2O_Concentration',
  nitrousOxideEmissionsDetermination: 'MEASUREMENT_N2O_Emissions',
  operationalManagement: 'MEASUREMENT_N2O_Operational',
  quantityMaterials: 'MEASUREMENT_N2O_Materials',
  quantityProductDetermination: 'MEASUREMENT_N2O_Product',
  referenceDetermination: 'MEASUREMENT_N2O_Reference',
  hasTransfer: 'MEASUREMENT_N2O_Transfer', //TODO is this used?
};

const fallbackMonitoringApproachMapper: Record<
  keyof Omit<FallbackMonitoringApproach, 'sourceStreamCategoryAppliedTiers' | 'type' | 'justification' | 'files'>,
  string
> = {
  annualUncertaintyAnalysis: 'FALLBACK_Uncertainty',
  approachDescription: 'FALLBACK_Description',
};

const measMonitoringApproachMapper: Record<
  keyof Omit<MeasurementOfCO2MonitoringApproach, 'type' | 'emissionPointCategoryAppliedTiers'>,
  string
> = {
  approachDescription: 'MEASUREMENT_CO2_Description',
  biomassEmissions: 'MEASUREMENT_CO2_Biomass',
  corroboratingCalculations: 'MEASUREMENT_CO2_Corroborating',
  emissionDetermination: 'MEASUREMENT_CO2_Emission',
  gasFlowCalculation: 'MEASUREMENT_CO2_Gasflow',
  referencePeriodDetermination: 'MEASUREMENT_CO2_Reference',
  hasTransfer: 'MEASUREMENT_N2O_Transfer', //TODO is it correct????? is used?
};

const inherentCO2MonitoringApproachMapper: Record<
  keyof Pick<InherentCO2MonitoringApproach, 'inherentReceivingTransferringInstallations'>,
  string
> = {
  inherentReceivingTransferringInstallations: 'INHERENT_CO2',
};

const transferredCO2MonitoringApproachMapper: Record<
  keyof Omit<TransferredCO2AndN2OMonitoringApproach, 'type'>,
  string
> = {
  deductionsToAmountOfTransferredCO2: 'TRANSFERRED_CO2_N2O_Deductions',
  geologicalStorage: 'TRANSFERRED_CO2_N2O_Storage',
  monitoringTransportNetworkApproach: 'TRANSFERRED_CO2_N2O_Transport_Network_Approach',
  transportCO2AndN2OPipelineSystems: 'TRANSFERRED_CO2_N2O_Pipeline',
};

const PFCMonitoringApproachMapper: Record<
  keyof Omit<CalculationOfPFCMonitoringApproach, 'type' | 'sourceStreamCategoryAppliedTiers'>,
  string
> = {
  approachDescription: 'CALCULATION_PFC_Description',
  cellAndAnodeTypes: 'CALCULATION_PFC_Types',
  collectionEfficiency: 'CALCULATION_PFC_Efficiency',
  tier2EmissionFactor: 'CALCULATION_PFC_Tier2EmissionFactor',
};

// General mappers

export const approachMapper: Record<string, any> = {
  CALCULATION_CO2: calculationMonitoringApproachMapper,
  MEASUREMENT_N2O: N2OMonitoringApproachMapper,
  FALLBACK: fallbackMonitoringApproachMapper,
  MEASUREMENT_CO2: measMonitoringApproachMapper,
  INHERENT_CO2: inherentCO2MonitoringApproachMapper,
  TRANSFERRED_CO2_N2O: transferredCO2MonitoringApproachMapper,
  CALCULATION_PFC: PFCMonitoringApproachMapper,
};

export const sourceStreamMapper: Record<string, any> = {
  CALCULATION_CO2: calculationSourceStreamMapper,
  MEASUREMENT_N2O: N2OSourceStreamMapper,
  FALLBACK: fallbackStreamMapper,
  MEASUREMENT_CO2: measurementStreamMapper,
  CALCULATION_PFC: PFCSourceStreamMapper,
};

export const reviewSectionsCompletedStandardInitialized = {
  PERMIT_TYPE: true,
  INSTALLATION_DETAILS: true,
  FUELS_AND_EQUIPMENT: true,
  DEFINE_MONITORING_APPROACHES: true,
  UNCERTAINTY_ANALYSIS: true,
  MANAGEMENT_PROCEDURES: true,
  MONITORING_METHODOLOGY_PLAN: true,
  ADDITIONAL_INFORMATION: true,
  CONFIDENTIALITY_STATEMENT: true,
  determination: true,
};
