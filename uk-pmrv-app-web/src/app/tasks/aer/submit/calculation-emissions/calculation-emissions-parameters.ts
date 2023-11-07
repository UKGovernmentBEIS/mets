export const parametersOptionsMap: Array<{
  name: string;
  emissionParameterName?: string;
  label: string;
  options: Array<{ label: string; value: string }>;
  measurementUnits?: { name: string; values: Array<{ text: string; value: string }> };
}> = [
  {
    name: 'ACTIVITY_DATA',
    label: 'Activity data',
    options: [
      {
        label: 'Tier 4',
        value: 'TIER_4',
      },
      {
        label: 'Tier 3',
        value: 'TIER_3',
      },
      {
        label: 'Tier 2',
        value: 'TIER_2',
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
      },
    ],
    measurementUnits: {
      name: 'activityDataMeasurementUnit',
      values: [
        { text: 'normal cubic meter (Nm3)', value: 'NM3' },
        { text: 'tonnes', value: 'TONNES' },
      ],
    },
  },
  {
    name: 'NET_CALORIFIC_VALUE',
    emissionParameterName: 'netCalorificValue',
    label: 'Net calorific value',
    options: [
      {
        label: 'Tier 3',
        value: 'TIER_3',
      },
      {
        label: 'Tier 2b',
        value: 'TIER_2B',
      },
      {
        label: 'Tier 2a',
        value: 'TIER_2A',
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
      },
    ],
    measurementUnits: {
      name: 'ncvMeasurementUnit',
      values: [
        { text: 'GJ/Nm3', value: 'GJ_PER_NM3' },
        { text: 'GJ/Tonne', value: 'GJ_PER_TONNE' },
      ],
    },
  },
  {
    name: 'EMISSION_FACTOR',
    emissionParameterName: 'emissionFactor',
    label: 'Emission factor',
    options: [
      {
        label: 'Tier 3',
        value: 'TIER_3',
      },
      {
        label: 'Tier 2b',
        value: 'TIER_2B',
      },
      {
        label: 'Tier 2a',
        value: 'TIER_2A',
      },
      {
        label: 'Tier 2',
        value: 'TIER_2',
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
      },
    ],
    measurementUnits: {
      name: 'efMeasurementUnit',
      values: [
        { text: 'tCO2/Nm3', value: 'TONNES_OF_CO2_PER_NM3' },
        { text: 'tCO2/TJ', value: 'TONNES_OF_CO2_PER_TJ' },
        { text: 'tCO2/Tonne', value: 'TONNES_OF_CO2_PER_TONNE' },
      ],
    },
  },
  {
    name: 'OXIDATION_FACTOR',
    emissionParameterName: 'oxidationFactor',
    label: 'Oxidation factor',
    options: [
      {
        label: 'Tier 3',
        value: 'TIER_3',
      },
      {
        label: 'Tier 2',
        value: 'TIER_2',
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
      },
    ],
  },
  {
    name: 'CARBON_CONTENT',
    emissionParameterName: 'carbonContent',
    label: 'Carbon content',
    options: [
      {
        label: 'Tier 3',
        value: 'TIER_3',
      },
      {
        label: 'Tier 2b',
        value: 'TIER_2B',
      },
      {
        label: 'Tier 2a',
        value: 'TIER_2A',
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
      },
    ],
    measurementUnits: {
      name: 'carbonContentMeasurementUnit',
      values: [
        { text: 'tC/Tonne', value: 'TONNES_OF_CARBON_PER_TONNE' },
        { text: 'tC/Nm3', value: 'TONNES_OF_CARBON_PER_NM3' },
        { text: 'tCO2/Tonne', value: 'TONNES_OF_CO2_PER_TONNE' },
        { text: 'tCO2/Nm3', value: 'TONNES_OF_CO2_PER_NM3' },
      ],
    },
  },
  {
    name: 'CONVERSION_FACTOR',
    emissionParameterName: 'conversionFactor',
    label: 'Conversion factor',
    options: [
      {
        label: 'Tier 2',
        value: 'TIER_2',
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
      },
    ],
  },
  {
    name: 'BIOMASS_FRACTION',
    label: 'Biomass',
    options: [
      {
        label: 'Tier 3',
        value: 'TIER_3',
      },
      {
        label: 'Tier 2',
        value: 'TIER_2',
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
      },
    ],
  },
];
