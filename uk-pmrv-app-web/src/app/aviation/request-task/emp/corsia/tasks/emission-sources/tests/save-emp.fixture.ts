export const otherFuelExplanationValue = 'some fuel description';
export const saveEmpFixture = {
  requestTaskId: 1,
  requestTaskActionType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION',
  requestTaskActionPayload: {
    payloadType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_PAYLOAD',
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
            fuelTypes: ['JET_KEROSENE'],
            numberOfAircrafts: 12,
            subtype: 'An aircraft subtype',
          },
        ],
      },
      operatorDetails: {
        operatorName: 'AviationOp',
      },
    },
  },
};
export const saveEmpFixtureWithOtherExplanation = {
  requestTaskId: 1,
  requestTaskActionType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION',
  requestTaskActionPayload: {
    payloadType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_PAYLOAD',
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
            fuelTypes: ['JET_KEROSENE'],
            numberOfAircrafts: 12,
            subtype: 'An aircraft subtype',
          },
        ],
      },
      operatorDetails: {
        operatorName: 'AviationOp',
      },
    },
  },
};
export const saveEmpFixtureWithoutFumm = {
  requestTaskId: 1,
  requestTaskActionType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION',
  requestTaskActionPayload: {
    payloadType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_PAYLOAD',
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
            fuelTypes: ['JET_KEROSENE'],
            numberOfAircrafts: 12,
            subtype: 'An aircraft subtype',
          },
        ],
      },
      emissionsMonitoringApproach: {
        certEmissionsType: 'GREAT_CIRCLE_DISTANCE',
        monitoringApproachType: 'CERT_MONITORING',
        explanation: 'A simple explanation',
      },
      operatorDetails: {
        operatorName: 'AviationOp',
      },
    },
  },
};
export const saveEmpFixtureWithFumm = {
  requestTaskId: 1,
  requestTaskActionType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION',
  requestTaskActionPayload: {
    payloadType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_PAYLOAD',
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
            fuelTypes: ['JET_KEROSENE'],
            numberOfAircrafts: 12,
            subtype: 'An aircraft subtype',
          },
        ],
      },
      emissionsMonitoringApproach: {
        monitoringApproachType: 'FUEL_USE_MONITORING',
      },
      operatorDetails: {
        operatorName: 'AviationOp',
      },
    },
  },
};
