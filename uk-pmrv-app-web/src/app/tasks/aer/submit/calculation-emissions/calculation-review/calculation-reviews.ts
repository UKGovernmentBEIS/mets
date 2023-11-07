import { getNonSustainableBiomassEmissions } from '@shared/components/review-groups/emissions-summary-group/emissions-summary-group';
import { format } from '@shared/utils/bignumber.utils';
import BigNumber from 'bignumber.js';

import { GovukValidators } from 'govuk-components';

import { CalculationRegionalDataCalculationMethod } from 'pmrv-api';

import { parametersOptionsMap } from '../calculation-emissions-parameters';

export function getCalculationReviewFormControls(sourceStreamEmission, disabled) {
  const emissionCalculationParamValues = (sourceStreamEmission?.parameterCalculationMethod as any)
    ?.emissionCalculationParamValues;
  const containsBiomass = sourceStreamEmission?.biomassPercentages?.contains;

  return {
    calculationCorrect: [
      { value: emissionCalculationParamValues?.calculationCorrect ?? null, disabled },
      {
        validators: GovukValidators.required('Select yes if the calculated emissions are correct'),
      },
    ],
    reasonForProvidingManualEmissions: [
      { value: emissionCalculationParamValues?.providedEmissions?.reasonForProvidingManualEmissions ?? null, disabled },
      {
        validators: GovukValidators.required('Explain why you are providing your own emission figures'),
      },
    ],
    totalProvidedReportableEmissions: [
      { value: emissionCalculationParamValues?.providedEmissions?.totalProvidedReportableEmissions ?? null, disabled },
      {
        validators: [
          GovukValidators.required(
            `Enter the total reportable emissions ${containsBiomass ? `, including non-sustainable biomass` : ''}`,
          ),
          GovukValidators.maxDecimalsValidator(5),
        ],
      },
    ],

    ...(sourceStreamEmission?.biomassPercentages?.contains
      ? {
          totalProvidedSustainableBiomassEmissions: [
            {
              value:
                emissionCalculationParamValues?.providedEmissions?.totalProvidedSustainableBiomassEmissions ?? null,
              disabled,
            },
            {
              validators: [
                GovukValidators.required('Enter the total sustainable biomass emissions'),
                GovukValidators.maxDecimalsValidator(5),
              ],
            },
          ],
        }
      : []),
  };
}

export function getEmissionsElements(sourceStreamEmission, calculatedEmissions, calculationFactor = null) {
  const parameters = sourceStreamEmission?.parameterMonitoringTiers;
  const calculationActivityDataCalculationMethod =
    sourceStreamEmission?.parameterCalculationMethod?.calculationActivityDataCalculationMethod;

  const activityParameterMeasurementUnit = parametersOptionsMap
    .find((option) => option.name === 'ACTIVITY_DATA')
    ?.measurementUnits.values.find((measurementUnitValue) => {
      return measurementUnitValue.value === calculationActivityDataCalculationMethod?.measurementUnit;
    }).text;

  const activityDataParameterElement = [
    {
      label: parametersOptionsMap.find((option) => option.name === 'ACTIVITY_DATA')?.label,
      content:
        isRegionalDataSelected(sourceStreamEmission) && isCelcsius15Selected(sourceStreamEmission)
          ? [
              {
                label: 'Total fuel or material used:',
                value: `${calculationActivityDataCalculationMethod?.totalMaterial} ${activityParameterMeasurementUnit}`,
              },
              {
                label: 'Value adjusted to 0ºC standard conditions:',
                value: `${calculationActivityDataCalculationMethod?.activityData} ${activityParameterMeasurementUnit}`,
              },
              {
                label: 'Metering coefficient used for the adjustment:',
                value: calculationFactor,
              },
            ]
          : [
              {
                label: 'Total:',
                value: `${calculationActivityDataCalculationMethod?.totalMaterial} ${activityParameterMeasurementUnit}`,
              },
            ],
    },
  ];

  const biomassParamterElement = sourceStreamEmission?.biomassPercentages?.contains
    ? [
        {
          label: parametersOptionsMap.find((option) => option.name === 'BIOMASS_FRACTION')?.label,
          content: [
            {
              label: 'Sustainable biomass percentage:',
              value: `${sourceStreamEmission?.biomassPercentages?.biomassPercentage}%`,
            },
            {
              label: 'Non-sustainable biomass percentage:',
              value: `${sourceStreamEmission?.biomassPercentages?.nonSustainableBiomassPercentage}%`,
            },
          ],
        },
      ]
    : [];

  const otherParametersElements = parameters
    .filter((parameter) => parameter.type !== 'BIOMASS_FRACTION' && parameter.type !== 'ACTIVITY_DATA')
    .map((parameter) => {
      const parameterOptions = parametersOptionsMap.find(
        (parameterOptions) => parameter.type === parameterOptions.name,
      );

      const elementLabel = parameterOptions.label;
      const elementValue = calculatedEmissions[parameterOptions.emissionParameterName];
      const elementMeasurementUnit = parameterOptions?.measurementUnits?.values?.find((unitValue) => {
        return unitValue.value === calculatedEmissions?.[parameterOptions?.measurementUnits?.name];
      })?.text;

      return {
        label: elementLabel,
        content: [
          {
            label: 'Value:',
            value: `${elementValue} ${elementMeasurementUnit ?? ''}`,
          },
        ],
      };
    });

  const nonSustainableBiomass = sourceStreamEmission?.biomassPercentages?.contains
    ? format(
        getNonSustainableBiomassEmissions(
          calculatedEmissions.totalReportableEmissions,
          calculatedEmissions?.totalSustainableBiomassEmissions,
          sourceStreamEmission?.biomassPercentages?.nonSustainableBiomassPercentage,
        ),
      )
    : null;

  const totalEmissionsElement = [
    {
      label: `Calculated emissions`,
      content: [
        {
          label: `Reportable emissions: ${format(
            new BigNumber(calculatedEmissions.totalReportableEmissions),
          )} tonnes CO2e ${
            nonSustainableBiomass ? `(includes ${nonSustainableBiomass} t non-sustainable biomass)` : ''
          } `,
          value: ``,
        },
        ...(calculatedEmissions?.totalSustainableBiomassEmissions
          ? [
              {
                label: `Sustainable biomass: ${format(
                  new BigNumber(calculatedEmissions.totalSustainableBiomassEmissions),
                )} tonnes CO2e`,
                value: ``,
              },
            ]
          : []),
      ],
    },
  ];

  return [
    ...activityDataParameterElement,
    ...otherParametersElements,
    ...biomassParamterElement,
    ...totalEmissionsElement,
  ];
}

export function isRegionalDataSelected(sourceStreamEmission) {
  return sourceStreamEmission?.parameterCalculationMethod?.type === 'REGIONAL_DATA';
}

export function isCelcsius15Selected(sourceStreamEmission) {
  return (
    (sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod)
      ?.fuelMeteringConditionType === 'CELSIUS_15'
  );
}
