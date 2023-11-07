import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';

import { EmpDataGapsCorsia } from 'pmrv-api';

export const FUMPayload = {
  emissionsMonitoringPlan: {
    ...EmpCorsiaStoreDelegate.INITIAL_STATE,
    emissionsMonitoringApproach: {
      monitoringApproachType: 'FUEL_USE_MONITORING',
    },
  },
  empSectionsCompleted: {
    emissionSources: [true],
    emissionsMonitoringApproach: [true],
  },
} as any;

export const certPayload = {
  emissionsMonitoringPlan: {
    ...EmpCorsiaStoreDelegate.INITIAL_STATE,
    emissionsMonitoringApproach: {
      monitoringApproachType: 'CERT_MONITORING',
      certEmissionsType: 'GREAT_CIRCLE_DISTANCE',
      explanation: 'asd',
      supportingEvidenceFiles: [],
    },
  },
  empSectionsCompleted: {
    emissionSources: [true],
    emissionsMonitoringApproach: [true],
  },
} as any;
export const certPayloadPopulated = {
  emissionsMonitoringPlan: {
    ...EmpCorsiaStoreDelegate.INITIAL_STATE,
    emissionsMonitoringApproach: {
      monitoringApproachType: 'CERT_MONITORING',
      certEmissionsType: 'GREAT_CIRCLE_DISTANCE',
      explanation: 'asd',
      supportingEvidenceFiles: [],
    },
    dataGaps: {
      dataGaps: 'some data gaps',
      secondaryDataSources: 'some secondary data sources',
    },
  },
  empSectionsCompleted: {
    emissionSources: [true],
    emissionsMonitoringApproach: [true],
  },
} as any;
export const dataGapPayload = {
  dataGaps: 'some data gaps',
  secondaryDataSources: 'some secondary data sources',
  secondarySourcesDataGapsExist: false,
};
export const summaryPayload = {
  emissionsMonitoringPlan: {
    ...EmpCorsiaStoreDelegate.INITIAL_STATE,
    emissionsMonitoringApproach: {
      monitoringApproachType: 'CERT_MONITORING',
      certEmissionsType: 'GREAT_CIRCLE_DISTANCE',
      explanation: 'asd',
      supportingEvidenceFiles: [],
    },
    dataGaps: dataGapPayload,
  },
  empSectionsCompleted: {
    dataGaps: [false],
    emissionsMonitoringApproach: [true],
  },
} as any;
export function createDataGapsFixture(dataGaps: Partial<EmpDataGapsCorsia>) {
  return {
    requestTaskId: 1,
    requestTaskActionType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_PAYLOAD',
      empSectionsCompleted: {
        emissionSources: [false],
      },
      emissionsMonitoringPlan: {
        emissionsMonitoringPlan: {
          emissionMonitoringApproach: {
            monitoringApproachType: 'FUEL_USE_MONITORING',
          },
          dataGaps,
        },
        operatorDetails: 'AviationOp',
      },
    },
  };
}
