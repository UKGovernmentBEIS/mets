import { EmpDataGaps } from 'pmrv-api';

export const saveDataGaps = {
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
        dataGaps: {},
      },
      operatorDetails: {
        operatorName: 'AviationOp',
        crcoCode: '1231412',
      },
    },
  },
};
export function createDataGapsFixture(dataGaps: Partial<EmpDataGaps>) {
  return {
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
          dataGaps,
        },
        operatorDetails: {
          operatorName: 'AviationOp',
          crcoCode: '1231412',
        },
      },
    },
  };
}
