import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/emp/shared/util/emp.util';
import { EmpRequestTaskPayloadUkEts } from '@aviation/request-task/store';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import produce from 'immer';

import { EmpEmissionsMonitoringApproach } from 'pmrv-api';

export function applySideEffectsToManagementProcedures(
  payload: EmpRequestTaskPayloadUkEts,
  update: EmpEmissionsMonitoringApproach,
): EmpRequestTaskPayloadUkEts {
  const status = getTaskStatusByTaskCompletionState('managementProcedures', payload);
  return ['in progress', 'complete'].includes(status)
    ? produce(payload, (updatedPayload) => {
        const isSupportFacility = update.monitoringApproachType === 'EUROCONTROL_SUPPORT_FACILITY';
        const isSmallEmitters = update.monitoringApproachType === 'EUROCONTROL_SMALL_EMITTERS';
        const isFuelUse = update.monitoringApproachType === 'FUEL_USE_MONITORING';

        if (isSupportFacility) {
          handleSupportFacilityType(updatedPayload, status);
        } else if (isSmallEmitters) {
          handleSmallEmittersType(updatedPayload, status);
        } else if (isFuelUse) {
          handleFuelUseType(updatedPayload);
        }
      })
    : payload;
}

function handleDeleteFromFuelUseType(
  payload: EmpRequestTaskPayloadUkEts,
  previousType: EmpEmissionsMonitoringApproach['monitoringApproachType'],
) {
  if (previousType === 'FUEL_USE_MONITORING') {
    const managementProcedures = payload.emissionsMonitoringPlan.managementProcedures;
    delete managementProcedures.riskAssessmentFile;
    delete managementProcedures.upliftQuantityCrossChecks;

    const handleDependenciesFromFummToSa = payload.emissionsMonitoringPlan;
    delete handleDependenciesFromFummToSa.methodAProcedures;
    delete handleDependenciesFromFummToSa.methodBProcedures;
    delete handleDependenciesFromFummToSa.blockOnBlockOffMethodProcedures;
    delete handleDependenciesFromFummToSa.fuelUpliftMethodProcedures;
    delete handleDependenciesFromFummToSa.blockHourMethodProcedures;
  }
}

function handleSupportFacilityType(payload: EmpRequestTaskPayloadUkEts, previousStatus: TaskItemStatus) {
  const previousType = payload.emissionsMonitoringPlan.emissionsMonitoringApproach.monitoringApproachType;

  if (previousType !== 'EUROCONTROL_SUPPORT_FACILITY') {
    const managementProcedures = payload.emissionsMonitoringPlan.managementProcedures;
    delete managementProcedures.assignmentOfResponsibilities;
    delete managementProcedures.monitoringPlanAppropriateness;
    delete managementProcedures.dataFlowActivities;
    delete managementProcedures.qaMeteringAndMeasuringEquipment;
    delete managementProcedures.dataValidation;
    delete managementProcedures.correctionsAndCorrectiveActions;
    delete managementProcedures.controlOfOutsourcedActivities;
    delete managementProcedures.assessAndControlRisks;
    delete managementProcedures.environmentalManagementSystem;
  }
  handleDeleteFromFuelUseType(payload, previousType);

  payload.empSectionsCompleted.managementProcedures = [previousStatus === 'complete' ? true : false];
}

function handleSmallEmittersType(payload: EmpRequestTaskPayloadUkEts, previousStatus: TaskItemStatus) {
  const previousType = payload.emissionsMonitoringPlan.emissionsMonitoringApproach.monitoringApproachType;

  handleDeleteFromFuelUseType(payload, previousType);
  if (previousType === null || previousType === 'EUROCONTROL_SUPPORT_FACILITY') {
    payload.empSectionsCompleted.managementProcedures = [false];
  } else {
    payload.empSectionsCompleted.managementProcedures = [previousStatus === 'complete' ? true : false];
  }
}

function handleFuelUseType(payload: EmpRequestTaskPayloadUkEts) {
  const previousType = payload.emissionsMonitoringPlan.emissionsMonitoringApproach.monitoringApproachType;

  if (previousType !== 'FUEL_USE_MONITORING') {
    payload.empSectionsCompleted.managementProcedures = [false];
  }
}
