import { DigitizedPlan } from 'pmrv-api';

export const energyFlowsDigitizedPlan: DigitizedPlan = {
  energyFlows: {
    fuelInputFlows: {
      fuelInputDataSources: [
        {
          fuelInput: 'METHOD_MONITORING_PLAN',
          energyContent: 'CALCULATION_METHOD_MONITORING_PLAN',
          dataSourceNumber: '0',
        },
      ],
      methodologyAppliedDescription: 'description',
      hierarchicalOrder: { followed: true },
      supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c116'],
    },
    measurableHeatFlows: {
      measurableHeatFlowsRelevant: true,
      measurableHeatFlowsDataSources: [
        {
          dataSourceNumber: '0',
          quantification: 'LEGAL_METROLOGICAL_CONTROL_READING',
          net: 'MEASUREMENTS',
        },
      ],
      methodologyAppliedDescription: 'description',
      hierarchicalOrder: { followed: true },
      supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c118'],
    },
    wasteGasFlows: {
      wasteGasFlowsRelevant: true,
      wasteGasFlowsDataSources: [
        {
          dataSourceNumber: '0',
          quantification: 'METHOD_MONITORING_PLAN',
          energyContent: 'CALCULATION_METHOD_MONITORING_PLAN',
        },
      ],
      methodologyAppliedDescription: 'description',
      hierarchicalOrder: { followed: true },
      supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c118'],
    },
    electricityFlows: {
      electricityProduced: true,
      electricityFlowsDataSources: [
        {
          dataSourceNumber: '0',
          quantification: 'LEGAL_METROLOGICAL_CONTROL_READING',
        },
      ],
      methodologyAppliedDescription: 'description',
      hierarchicalOrder: { followed: true },
      supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c118'],
    },
  },
};
