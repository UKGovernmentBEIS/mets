import BigNumber from 'bignumber.js';

import {
  CalculationActivityDataAggregationMeteringCalcMethod,
  CalculationManualCalculationMethod,
  CalculationNationalInventoryDataCalculationMethod,
  CalculationRegionalDataCalculationMethod,
} from 'pmrv-api';

import { parametersOptionsMap } from '../calculation-emissions-parameters';
import { isCelcsius15Selected, isRegionalDataSelected } from '../calculation-review/calculation-reviews';

export function getFormData(sourceStreamEmission, nationalInventoryData, regionalInventoryData) {
  const calculationMethodType = sourceStreamEmission?.parameterCalculationMethod?.type;

  let parameterCalculationMethod;
  let predefinedMeasurementUnit;
  let calculationActivityDataCalculationMethod;

  if (calculationMethodType === 'NATIONAL_INVENTORY_DATA') {
    parameterCalculationMethod =
      sourceStreamEmission?.parameterCalculationMethod as CalculationNationalInventoryDataCalculationMethod;

    calculationActivityDataCalculationMethod =
      parameterCalculationMethod?.calculationActivityDataCalculationMethod as CalculationActivityDataAggregationMeteringCalcMethod;

    const selectedSector = parameterCalculationMethod?.mainActivitySector;
    const selectedFuel = parameterCalculationMethod?.fuel;

    const sector = nationalInventoryData?.sectors.find((sector) => sector.name === selectedSector);
    const sectorFuel = sector?.fuels.find((fuel) => fuel.name === selectedFuel);
    predefinedMeasurementUnit = sectorFuel?.emissionCalculationParameters?.ncvMeasurementUnit;
  } else if (calculationMethodType === 'REGIONAL_DATA') {
    parameterCalculationMethod =
      sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod;

    calculationActivityDataCalculationMethod =
      parameterCalculationMethod?.calculationActivityDataCalculationMethod as CalculationActivityDataAggregationMeteringCalcMethod;

    predefinedMeasurementUnit = regionalInventoryData.ncvMeasurementUnit;
  } else {
    parameterCalculationMethod = sourceStreamEmission?.parameterCalculationMethod as CalculationManualCalculationMethod;
    calculationActivityDataCalculationMethod =
      parameterCalculationMethod?.calculationActivityDataCalculationMethod as CalculationActivityDataAggregationMeteringCalcMethod;
  }

  return [predefinedMeasurementUnit, calculationActivityDataCalculationMethod];
}

export function getActivityCalculationNextStep(sourceStreamEmission) {
  let nextStep = '';

  const containsBiomass = sourceStreamEmission?.biomassPercentages?.contains;
  const filledInventoryDataFields =
    ['NATIONAL_INVENTORY_DATA', 'REGIONAL_DATA'].includes(sourceStreamEmission?.parameterCalculationMethod.type) &&
    (!!(sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod)?.localZoneCode ||
      !!(sourceStreamEmission?.parameterCalculationMethod as CalculationNationalInventoryDataCalculationMethod)
        ?.mainActivitySector);

  if (filledInventoryDataFields) {
    if (containsBiomass) {
      nextStep = 'biomass-calculation';
    } else {
      nextStep = 'inventory-data-review';
    }
  } else {
    nextStep = 'manual-calculation-values';
  }
  return nextStep;
}

export function getActivityDataMeasuremenUnits() {
  return parametersOptionsMap.find((parametersOptionsMap) => parametersOptionsMap.name === 'ACTIVITY_DATA')
    ?.measurementUnits.values;
}

export function calculateActivityData(sourceStreamEmission, totalMaterial, calculationFactor) {
  const celcsius15Selected = isCelcsius15Selected(sourceStreamEmission);
  const regionalDataSelected = isRegionalDataSelected(sourceStreamEmission);

  const bigTotalMaterial = new BigNumber(totalMaterial);
  const bigCalculationFactor = new BigNumber(calculationFactor);

  const activityData =
    regionalDataSelected && celcsius15Selected ? bigTotalMaterial.multipliedBy(bigCalculationFactor) : bigTotalMaterial;

  return activityData.toString();
}
