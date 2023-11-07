export const otherFuelExplanationValue = 'some fuel description';
export const saveEmpFixture = {
  requestTaskId: 1,
  requestTaskActionType: 'EMP_ISSUANCE_UKETS_SAVE_APPLICATION',
  requestTaskActionPayload: {
    payloadType: 'EMP_ISSUANCE_UKETS_SAVE_APPLICATION_PAYLOAD',
    empSectionsCompleted: {
      emissionSources: [false],
    },
    emissionsMonitoringPlan: {
      emissionSources: {
        aircraftTypes: [
          {
            aircraftTypeInfo: {
              manufacturer: '(any manufacturer)',
              model: 'Glider',
              designatorType: 'GLID',
            },
            fuelTypes: ['JET_KEROSENE', 'OTHER'],
            isCurrentlyUsed: true,
            numberOfAircrafts: 12,
            subtype: 'An aircraft subtype',
          },
        ],
      },
      operatorDetails: {
        operatorName: 'AviationOp',
        crcoCode: '1231412',
      },
    },
  },
};
export const saveEmpFixtureWithOtherExplanation = {
  requestTaskId: 1,
  requestTaskActionType: 'EMP_ISSUANCE_UKETS_SAVE_APPLICATION',
  requestTaskActionPayload: {
    payloadType: 'EMP_ISSUANCE_UKETS_SAVE_APPLICATION_PAYLOAD',
    empSectionsCompleted: {
      emissionSources: [false],
    },
    emissionsMonitoringPlan: {
      emissionSources: {
        aircraftTypes: [
          {
            aircraftTypeInfo: {
              manufacturer: '(any manufacturer)',
              model: 'Glider',
              designatorType: 'GLID',
            },
            fuelTypes: ['JET_KEROSENE', 'OTHER'],
            isCurrentlyUsed: true,
            numberOfAircrafts: 12,
            subtype: 'An aircraft subtype',
          },
        ],
        otherFuelExplanation: otherFuelExplanationValue,
      },
      operatorDetails: {
        operatorName: 'AviationOp',
        crcoCode: '1231412',
      },
    },
  },
};
export const saveEmpFixtureWithoutFumm = {
  requestTaskId: 1,
  requestTaskActionType: 'EMP_ISSUANCE_UKETS_SAVE_APPLICATION',
  requestTaskActionPayload: {
    payloadType: 'EMP_ISSUANCE_UKETS_SAVE_APPLICATION_PAYLOAD',
    empSectionsCompleted: {
      emissionSources: [false],
    },
    emissionsMonitoringPlan: {
      emissionSources: {
        aircraftTypes: [
          {
            aircraftTypeInfo: {
              manufacturer: '(any manufacturer)',
              model: 'Glider',
              designatorType: 'GLID',
            },
            fuelTypes: ['JET_KEROSENE', 'OTHER'],
            isCurrentlyUsed: true,
            numberOfAircrafts: 12,
            subtype: 'An aircraft subtype',
          },
        ],
      },
      emissionsMonitoringApproach: {
        monitoringApproachType: 'EUROCONTROL_SMALL_EMITTERS',
        explanation: 'A simple explanation',
      },
      operatorDetails: {
        operatorName: 'AviationOp',
        crcoCode: '1231412',
      },
    },
  },
};
export const saveEmpFixtureWithFumm = {
  requestTaskId: 1,
  requestTaskActionType: 'EMP_ISSUANCE_UKETS_SAVE_APPLICATION',
  requestTaskActionPayload: {
    payloadType: 'EMP_ISSUANCE_UKETS_SAVE_APPLICATION_PAYLOAD',
    empSectionsCompleted: {
      emissionSources: [false],
    },
    emissionsMonitoringPlan: {
      emissionSources: {
        aircraftTypes: [
          {
            aircraftTypeInfo: {
              manufacturer: '(any manufacturer)',
              model: 'Glider',
              designatorType: 'GLID',
            },
            fuelTypes: ['JET_KEROSENE', 'OTHER'],
            isCurrentlyUsed: true,
            numberOfAircrafts: 12,
            subtype: 'An aircraft subtype',
          },
        ],
      },
      emissionsMonitoringPlan: {
        emissionsMonitoringApproach: {
          monitoringApproachType: 'FUEL_USE_MONITORING',
        },
      },
      operatorDetails: {
        operatorName: 'AviationOp',
        crcoCode: '1231412',
      },
    },
  },
};
